package dev.abhishek.ecommerce.modules.auth;

import dev.abhishek.ecommerce.modules.auth.authDTO.AuthRequest;
import dev.abhishek.ecommerce.modules.auth.authDTO.RegisterRequest;
import dev.abhishek.ecommerce.modules.auth.authDTO.AuthResponse;
import dev.abhishek.ecommerce.modules.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
}
