package dev.abhishek.ecommerce.modules.auth.event;

import dev.abhishek.ecommerce.modules.auth.authDTO.PasswordResetConfirmDTO;
import dev.abhishek.ecommerce.modules.user.model.User;

public record PasswordResetConfirmEvent(User user) {
}
