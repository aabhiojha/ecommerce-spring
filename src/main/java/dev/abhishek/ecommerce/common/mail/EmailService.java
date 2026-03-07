package dev.abhishek.ecommerce.common.mail;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendPlainText(String to, String subject, String body);
    void sendHtml(String to, String subject, String htmlBody) throws MessagingException;
}
