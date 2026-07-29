package dev.abhishek.ecommerce.common.mail;

import java.util.Map;

public interface EmailService {

    void sendPlainText(String to, String subject, String body);

    /**
     * Renders {@code templateName} from /templates, substituting {{placeholders}} from {@code model}.
     */
    void sendHtml(String to, String subject, String templateName, Map<String, String> model);
}
