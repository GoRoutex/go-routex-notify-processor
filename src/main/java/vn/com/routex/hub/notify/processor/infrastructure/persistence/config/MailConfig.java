package vn.com.routex.hub.notify.processor.infrastructure.persistence.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SendGridMailProperties.class)
public class MailConfig {
}
