package vn.com.routex.hub.notify.processor.application.services.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import vn.com.go.routex.identity.security.log.SystemLog;
import vn.com.routex.hub.notify.processor.application.services.EmailService;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.config.BrevoMailProperties;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final TemplateEngine templateEngine;
    private final BrevoMailProperties properties;
    private final JavaMailSender mailSender;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    public void sendEmail(String toEmail, String subject, String templateName, Map<String, Object> variables) {
        sLog.info("[EMAIL-SERVICE] Preparing email to={}, subject={}, template={}", toEmail, subject, templateName);
        validateConfiguration();

        Context context = new Context();
        context.setVariables(variables);
        String htmlBody = templateEngine.process(templateName, context);

        String emailSubject = (subject == null || subject.isBlank()) ? properties.getVerifySubject() : subject;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(properties.getFromEmail(), properties.getFromName());
            helper.setTo(toEmail);
            helper.setSubject(emailSubject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            sLog.info("[EMAIL-SERVICE] Brevo SMTP accepted email to={}", toEmail);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to send email via Brevo SMTP", ex);
        }
    }

    private void validateConfiguration() {
        if (isBlank(properties.getFromEmail())) {
            throw new IllegalStateException("Brevo email configuration is incomplete");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
