package dev.abhishek.ecommerce.modules.auth.event;

import dev.abhishek.ecommerce.modules.auth.authDTO.PasswordResetDTO;

public record PasswordResetEvent(PasswordResetDTO passwordResetDTO) {
}
