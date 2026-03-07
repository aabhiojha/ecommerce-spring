package dev.abhishek.ecommerce.common.mail;

import dev.abhishek.ecommerce.modules.auth.authDTO.PasswordResetDTO;
import dev.abhishek.ecommerce.modules.auth.event.PasswordResetEvent;
import dev.abhishek.ecommerce.modules.auth.event.UserRegisteredEvent;
import dev.abhishek.ecommerce.modules.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailListener {
    private final EmailServiceImpl mailService;

    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event){
        User user = event.user();
        log.debug("The user {} has successfully registered", user.getUsername());
    }

    @EventListener
    public void handlePasswordReset(PasswordResetEvent event){
        PasswordResetDTO passwordResetDTO = event.passwordResetDTO();
        log.debug("The password reset code has been sent to the email {}", passwordResetDTO);
    }

}
