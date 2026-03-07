package dev.abhishek.ecommerce.modules.auth.event;

public record PasswordResetEvent(String email, Integer token) {
}
