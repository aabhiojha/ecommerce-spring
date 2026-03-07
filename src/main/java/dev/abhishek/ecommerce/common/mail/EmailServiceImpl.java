package dev.abhishek.ecommerce.common.mail;

import dev.abhishek.ecommerce.modules.auth.controller.AuthController;
import dev.abhishek.ecommerce.modules.auth.model.PasswordResetToken;
import dev.abhishek.ecommerce.modules.auth.repository.PasswordResetTokenRepository;
import dev.abhishek.ecommerce.modules.user.model.User;
import dev.abhishek.ecommerce.modules.user.repository.UserRepository;
import dev.abhishek.ecommerce.modules.user.service.UserServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final UserServiceImpl userServiceImpl;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public void sendPlainText(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.debug("The mail is sent: {}", (Object) message.getTo());
        } catch (Exception e) {
            log.error("Error while sending mail");
        }
    }

    @Override
    public void sendHtml(String to, String subject, String templateName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);

        // lets get the user object as well
        User user = userRepository.findByEmailIgnoreCase(to).orElse(null);
        List<PasswordResetToken> byUser = passwordResetTokenRepository.findByUserAndUsedNot(user, false);

        try {
            String template = loadTemplate(templateName);
            switch (templateName) {
                case "welcome-email.html":
                    helper.setText(
                            template.replace("{{username}}", user.getUsername()),
                            true
                    );

                case "password-reset.html":
                    helper.setText(
                            template.replace("{{RESET_CODE}}", (String) byUser.getFirst().getToken()),
                            true
                    );

            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mailSender.send(message);
        log.debug("The email is sent successfully");
    }

    private String loadTemplate(String templateName) throws IOException {
        return new String(
                AuthController.class.getResourceAsStream("/templates/" + templateName).readAllBytes()
        );
    }

}
