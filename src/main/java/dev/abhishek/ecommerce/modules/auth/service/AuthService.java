package dev.abhishek.ecommerce.modules.auth.service;

import dev.abhishek.ecommerce.modules.auth.authDTO.AuthRequest;
import dev.abhishek.ecommerce.modules.auth.authDTO.AuthResponse;
import dev.abhishek.ecommerce.modules.auth.authDTO.PasswordResetConfirmDTO;
import dev.abhishek.ecommerce.modules.auth.authDTO.PasswordResetDTO;
import dev.abhishek.ecommerce.modules.auth.authDTO.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse authenticate(AuthRequest request);

    void passwordReset(PasswordResetDTO passwordResetDTO);

    void passwordResetConfirm(PasswordResetConfirmDTO passwordResetConfirmDTO);
}
