package dev.abhishek.ecommerce.common.mail;

import dev.abhishek.ecommerce.modules.auth.event.PasswordResetConfirmEvent;
import dev.abhishek.ecommerce.modules.auth.event.PasswordResetEvent;
import dev.abhishek.ecommerce.modules.auth.event.UserRegisteredEvent;
import dev.abhishek.ecommerce.modules.user.model.User;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailListener {
    private final EmailServiceImpl mailService;
    private final EmailService emailService;

    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) throws MessagingException {
        User user = event.user();
        log.debug("The user {} has successfully registered", user.getUsername());
//        mailService.sendPlainText(user.getEmail(), "Welcome to the website", "welcome mail.");
        mailService.sendHtml(user.getEmail(), "Welcome to the website", "welcome-email.html");
    }

    @EventListener
    public void handlePasswordReset(PasswordResetEvent event) throws MessagingException {

        String email = event.email();

        mailService.sendHtml(email, "Password update request OTP", "password-reset.html");
        log.debug("Password reset code sent to {}", event.email());
    }

    @EventListener
    public void handlePasswordResetConfirmation(PasswordResetConfirmEvent event) throws MessagingException {
        User user = event.user();

        mailService.sendHtml(user.getEmail(), "Password reset successful", "password-reset-confimation.html");
        log.debug("Password reset notice sent to email {}", user.getEmail());
    }

}
