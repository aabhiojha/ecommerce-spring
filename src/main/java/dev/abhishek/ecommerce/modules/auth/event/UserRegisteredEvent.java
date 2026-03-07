package dev.abhishek.ecommerce.modules.auth.event;

import dev.abhishek.ecommerce.modules.user.model.User;

public record UserRegisteredEvent(User user) {
}
