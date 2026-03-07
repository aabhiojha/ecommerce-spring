package dev.abhishek.ecommerce.modules.auth.controller;

import dev.abhishek.ecommerce.modules.auth.authDTO.*;
import dev.abhishek.ecommerce.modules.auth.service.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        // Register new user and return JWT
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        // Authenticate and return JWT
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/password-reset")
    public ResponseEntity<?> passwordReset(@RequestBody PasswordResetDTO request) {
        authService.password_reset(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/password-reset-confirm")
    public ResponseEntity<?> passwordResetConfirm(@RequestBody PasswordResetConfirmDTO passwordResetConfirmDTO){
        authService.password_reset_confirm(passwordResetConfirmDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
