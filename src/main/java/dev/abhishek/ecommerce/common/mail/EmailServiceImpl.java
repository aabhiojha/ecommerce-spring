package dev.abhishek.ecommerce.common.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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
            log.debug("Plain text mail sent to {}", to);
        } catch (MailException ex) {
            log.error("Could not send plain text mail to {}", to, ex);
        }
    }

    @Override
    public void sendHtml(String to, String subject, String templateName, Map<String, String> model) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(render(templateName, model), true);

            mailSender.send(message);
            log.debug("Template {} sent to {}", templateName, to);
        } catch (MessagingException | MailException | IOException ex) {
            // A failed notification must never fail the request that triggered it.
            log.error("Could not send template {} to {}", templateName, to, ex);
        }
    }

    private String render(String templateName, Map<String, String> model) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/" + templateName);
        if (!resource.exists()) {
            throw new IOException("Unknown mail template: " + templateName);
        }

        String template;
        try (InputStream inputStream = resource.getInputStream()) {
            template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        for (Map.Entry<String, String> entry : model.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue() == null ? "" : entry.getValue());
        }
        return template;
    }
}
