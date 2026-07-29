package dev.abhishek.ecommerce.modules.auth.controller;

import dev.abhishek.ecommerce.modules.auth.authDTO.AuthRequest;
import dev.abhishek.ecommerce.modules.auth.authDTO.AuthResponse;
import dev.abhishek.ecommerce.modules.auth.authDTO.PasswordResetConfirmDTO;
import dev.abhishek.ecommerce.modules.auth.authDTO.PasswordResetDTO;
import dev.abhishek.ecommerce.modules.auth.authDTO.RegisterRequest;
import dev.abhishek.ecommerce.modules.auth.authDTO.RefreshTokenRequest;
import dev.abhishek.ecommerce.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Endpoints for user registration, login, and password reset")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user", description = "Creates a new user account and returns a JWT token")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // Register new user and return JWT
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Authenticate user", description = "Logs in a user and returns a JWT token")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request) {
        // Authenticate and return JWT
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @Operation(summary = "Refresh token", description = "Refreshes a JWT token using a refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @Operation(summary = "Request password reset", description = "Sends a password reset email if the account exists")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/password-reset")
    public ResponseEntity<Void> passwordReset(@Valid @RequestBody PasswordResetDTO request) {
        authService.passwordReset(request);
        // Always 202, whether or not the address is registered.
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Confirm password reset", description = "Resets the user's password using the provided token")
    @PostMapping("/password-reset-confirm")
    public ResponseEntity<Void> passwordResetConfirm(@Valid @RequestBody PasswordResetConfirmDTO request) {
        authService.passwordResetConfirm(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
