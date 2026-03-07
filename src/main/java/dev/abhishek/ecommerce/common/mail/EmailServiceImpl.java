package dev.abhishek.ecommerce.common.mail;

import dev.abhishek.ecommerce.modules.auth.controller.AuthController;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

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
        }catch (Exception e){
            log.error("Error while sending mail");
        }
    }

    @Override
    public void sendHtml(String to, String subject, String templateName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,"UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);

        try(var inputStream = AuthController.class.getResourceAsStream("/templates/"+templateName)) {
            helper.setText(
                    new String(inputStream.readAllBytes(), StandardCharsets.UTF_8),
                    true
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mailSender.send(message);
        log.debug("The email is sent successfully");
    }


}
