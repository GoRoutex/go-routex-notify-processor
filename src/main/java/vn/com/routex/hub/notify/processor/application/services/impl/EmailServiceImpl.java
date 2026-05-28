package vn.com.routex.hub.notify.processor.application.services.impl;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import vn.com.go.routex.identity.security.log.SystemLog;
import vn.com.routex.hub.notify.processor.application.services.EmailService;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.config.SendGridMailProperties;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final TemplateEngine templateEngine;
    private final SendGridMailProperties properties;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    public void sendEmail(String toEmail, String subject, String templateName, Map<String, Object> variables) {
        sLog.info("[EMAIL-SERVICE] Preparing email to={}, subject={}, template={}", toEmail, subject, templateName);

        Context context = new Context();
        context.setVariables(variables);
        String htmlBody = templateEngine.process(templateName, context);

        Email from = new Email(properties.getFromEmail(), properties.getFromName());
        Email to = new Email(toEmail);
        Content content = new Content("text/html", htmlBody);

        String emailSubject = (subject == null || subject.isBlank()) ? properties.getVerifySubject() : subject;
        Mail mail = new Mail(from, emailSubject, to, content);

        SendGrid sendGrid = new SendGrid(properties.getApiKey());
        Request emailRequest = new Request();

        try {
            emailRequest.setMethod(Method.POST);
            emailRequest.setEndpoint("mail/send");
            emailRequest.setBody(mail.build());

            Response response = sendGrid.api(emailRequest);

            sLog.info("[EMAIL-SERVICE] SendGrid response status: {}", response.getStatusCode());
            if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
                throw new IllegalStateException(
                        "SendGrid send mail failed. Status=%s, body=%s"
                                .formatted(response.getStatusCode(), response.getBody())
                );
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to send email via SendGrid", ex);
        }
    }
}
