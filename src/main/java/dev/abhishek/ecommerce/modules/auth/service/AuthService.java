package dev.abhishek.ecommerce.modules.auth.service;

import dev.abhishek.ecommerce.modules.auth.authDTO.*;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse authenticate(AuthRequest request);
    void password_reset(PasswordResetDTO passwordResetDTO);
    void password_reset_confirm(PasswordResetConfirmDTO passwordResetConfirmDTO);
}
