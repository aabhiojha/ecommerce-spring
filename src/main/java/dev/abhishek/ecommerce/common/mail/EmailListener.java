package dev.abhishek.ecommerce.common.mail;

import dev.abhishek.ecommerce.modules.auth.event.PasswordResetConfirmEvent;
import dev.abhishek.ecommerce.modules.auth.event.PasswordResetEvent;
import dev.abhishek.ecommerce.modules.auth.event.UserRegisteredEvent;
import dev.abhishek.ecommerce.modules.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

/**
 * Mails are sent after the triggering transaction commits, so a rolled back
 * registration or password reset never produces an email.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailListener {

    private final EmailService emailService;

    @Async
    @TransactionalEventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        User user = event.user();
        log.debug("The user {} has successfully registered", user.getUsername());
        emailService.sendHtml(
                user.getEmail(),
                "Welcome to the website",
                "welcome-email.html",
                Map.of("username", user.getUsername())
        );
    }

    @Async
    @TransactionalEventListener
    public void handlePasswordReset(PasswordResetEvent event) {
        emailService.sendHtml(
                event.email(),
                "Password update request OTP",
                "password-reset.html",
                Map.of("RESET_CODE", String.valueOf(event.token()))
        );
        log.debug("Password reset code sent");
    }

    @Async
    @TransactionalEventListener
    public void handlePasswordResetConfirmation(PasswordResetConfirmEvent event) {
        User user = event.user();
        emailService.sendHtml(
                user.getEmail(),
                "Password reset successful",
                "password-reset-confimation.html",
                Map.of("username", user.getUsername())
        );
        log.debug("Password reset notice sent");
    }
}
