package dev.abhishek.ecommerce.modules.auth.service;

import dev.abhishek.ecommerce.common.helpers.RandomNumbers;
import dev.abhishek.ecommerce.common.security.jtw.JwtService;
import dev.abhishek.ecommerce.modules.auth.authDTO.AuthRequest;
import dev.abhishek.ecommerce.modules.auth.authDTO.AuthResponse;
import dev.abhishek.ecommerce.modules.auth.authDTO.PasswordResetConfirmDTO;
import dev.abhishek.ecommerce.modules.auth.authDTO.PasswordResetDTO;
import dev.abhishek.ecommerce.modules.auth.authDTO.RefreshTokenRequest;
import dev.abhishek.ecommerce.modules.auth.authDTO.RegisterRequest;
import dev.abhishek.ecommerce.modules.auth.event.PasswordResetConfirmEvent;
import dev.abhishek.ecommerce.modules.auth.event.PasswordResetEvent;
import dev.abhishek.ecommerce.modules.auth.event.UserRegisteredEvent;
import dev.abhishek.ecommerce.modules.auth.model.PasswordResetToken;
import dev.abhishek.ecommerce.modules.auth.model.RefreshToken;
import dev.abhishek.ecommerce.modules.auth.model.Role;
import dev.abhishek.ecommerce.modules.auth.repository.PasswordResetTokenRepository;
import dev.abhishek.ecommerce.modules.auth.repository.RoleRepository;
import dev.abhishek.ecommerce.modules.user.model.User;
import dev.abhishek.ecommerce.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final int RESET_TOKEN_TTL_MINUTES = 30;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final ApplicationEventPublisher publisher;
    private final RefreshTokenService refreshTokenService;

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String jwt = jwtService.generateToken(user);
                    List<String> roles = user.getRoles().stream()
                            .map(Role::getName)
                            .toList();
                    return new AuthResponse(jwt, request.getRefreshToken(), user.getUsername(), roles);
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUserName(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (request.getEmail() != null && userRepository.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create new user with encoded password
        User user = new User();
        user.setUserName(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);

        Role defaultRole = roleRepository.findByNameIgnoreCase("ROLE_CUSTOMER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_CUSTOMER")));
        user.getRoles().add(defaultRole);

        User savedUser = userRepository.save(user);

        // Generate JWT for immediate login after registration
        String jwt = jwtService.generateToken(savedUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getUsername());
        List<String> roles = savedUser.getRoles().stream().map(Role::getName).toList();

        publisher.publishEvent(new UserRegisteredEvent(savedUser));
        return new AuthResponse(jwt, refreshToken.getToken(), savedUser.getUsername(), roles);
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        // Let Spring Security validate credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // If we get here, credentials are valid
        User user = userRepository.findByUserName(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Generate and return JWT
        String jwt = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();
        return new AuthResponse(jwt, refreshToken.getToken(), user.getUsername(), roles);
    }

    @Override
    @Transactional
    public void passwordReset(PasswordResetDTO passwordResetDTO) {
        String email = passwordResetDTO.getEmail();
        User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
        if (user == null) {
            // Answer identically for unknown addresses so the endpoint cannot enumerate accounts.
            log.debug("Password reset requested for unknown address");
            return;
        }

        // Any token still outstanding is invalidated, so only the newest code works.
        passwordResetTokenRepository.deleteByUser(user);

        PasswordResetToken pst = PasswordResetToken.builder()
                .user(user)
                .token(generateUniqueResetToken())
                .expiresAt(LocalDateTime.now().plusMinutes(RESET_TOKEN_TTL_MINUTES))
                .used(false)
                .build();

        passwordResetTokenRepository.saveAndFlush(pst);
        log.debug("Password reset token issued for userId={}", user.getId());
        publisher.publishEvent(new PasswordResetEvent(user.getEmail(), pst.getToken()));
    }

    /**
     * The token column is unique, so a collision with a live token would fail the insert.
     */
    private Integer generateUniqueResetToken() {
        for (int attempt = 0; attempt < 10; attempt++) {
            Integer candidate = RandomNumbers.generateResetToken();
            if (!passwordResetTokenRepository.existsByToken(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Could not generate a unique password reset token");
    }

    @Override
    @Transactional
    public void passwordResetConfirm(PasswordResetConfirmDTO passwordResetConfirmDTO) {
        Optional<PasswordResetToken> maybeToken =
                passwordResetTokenRepository.findByToken(passwordResetConfirmDTO.getToken());

        // A wrong, used or expired code all fail the same way; the caller learns nothing extra.
        PasswordResetToken token = maybeToken
                .filter(candidate -> !Boolean.TRUE.equals(candidate.getUsed()))
                .filter(candidate -> candidate.getExpiresAt() != null
                        && candidate.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new IllegalArgumentException("The reset code is invalid or has expired"));

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(passwordResetConfirmDTO.getPassword()));
        userRepository.save(user);

        token.setUsed(true);
        passwordResetTokenRepository.save(token);

        log.debug("Password reset completed for userId={}", user.getId());
        publisher.publishEvent(new PasswordResetConfirmEvent(user));
    }
}
