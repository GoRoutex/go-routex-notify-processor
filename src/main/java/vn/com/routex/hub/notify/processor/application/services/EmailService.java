package vn.com.routex.hub.notify.processor.application.services;

import java.util.Map;

public interface EmailService {
    void sendEmail(String toEmail, String subject, String templateName, Map<String, Object> variables);
}
