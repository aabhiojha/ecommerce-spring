package dev.abhishek.ecommerce.modules.auth.service;

import dev.abhishek.ecommerce.common.security.jtw.JwtService;
import dev.abhishek.ecommerce.modules.auth.authDTO.AuthRequest;
import dev.abhishek.ecommerce.modules.auth.authDTO.RegisterRequest;
import dev.abhishek.ecommerce.modules.auth.authDTO.AuthResponse;
import dev.abhishek.ecommerce.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // Create new user with encoded password
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        // Save to database
        userRepository.save(user);

        // Generate JWT for immediate login after registration
        var jwt = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwt)
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        // Let Spring Security validate credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // If we get here, credentials are valid
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        // Generate and return JWT
        var jwt = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwt)
                .build();
    }

}
