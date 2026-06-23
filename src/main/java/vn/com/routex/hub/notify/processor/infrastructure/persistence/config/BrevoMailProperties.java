package vn.com.routex.hub.notify.processor.infrastructure.persistence.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "notification.email.brevo")
public class BrevoMailProperties {
    private String fromEmail;
    private String fromName;
    private String verifySubject;
    private String ticketSubject;
}
