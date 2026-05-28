package vn.com.routex.hub.notify.processor.infrastructure.kafka.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.com.go.routex.identity.security.log.SystemLog;
import vn.com.routex.hub.notify.processor.application.services.EmailService;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.EmailNotificationEvent;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.model.KafkaEventMessage;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.exception.BusinessException;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.utils.ExceptionUtils;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.utils.JsonUtils;

import static vn.com.routex.hub.notify.processor.infrastructure.persistence.constant.ErrorConstant.INVALID_DATA_ERROR_MESSAGE;
import static vn.com.routex.hub.notify.processor.infrastructure.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;

@Component
@RequiredArgsConstructor
public class EmailNotificationConsumer {

    private final EmailService emailService;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @KafkaListener(
            topics = "${spring.kafka.events.notification-email}",
            groupId = "notify-processor-email"
    )
    public void handle(String payload) {
        sLog.info("[EMAIL-CONSUMER] Received email payload: {}", payload);
        KafkaEventMessage<EmailNotificationEvent> event = JsonUtils.parseToKafkaObject(
                payload,
                new TypeReference<>() {
                }
        );

        if (event == null || event.data() == null) {
            throw new BusinessException(ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_DATA_ERROR_MESSAGE));
        }

        EmailNotificationEvent data = event.data();
        if (isBlank(data.toEmail()) || isBlank(data.templateName())) {
            sLog.error("[EMAIL-CONSUMER] Invalid email event data: recipient and template are required");
            return;
        }

        try {
            emailService.sendEmail(
                    data.toEmail(),
                    data.subject(),
                    data.templateName(),
                    data.variables()
            );
            sLog.info("[EMAIL-CONSUMER] Successfully processed email notification to={}", data.toEmail());
        } catch (Exception e) {
            sLog.error("[EMAIL-CONSUMER] Error processing email notification to={}", data.toEmail(), e);
            throw e;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
