package vn.com.routex.hub.notify.processor.infrastructure.kafka.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.com.go.routex.identity.security.log.SystemLog;
import vn.com.routex.hub.notify.processor.application.services.EmailService;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.EmailNotificationEvent;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.model.KafkaEventMessage;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.exception.BusinessException;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.utils.ExceptionUtils;

import static vn.com.routex.hub.notify.processor.infrastructure.persistence.constant.ErrorConstant.INVALID_DATA_ERROR_MESSAGE;
import static vn.com.routex.hub.notify.processor.infrastructure.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;

@Component
@RequiredArgsConstructor
public class EmailNotificationConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @KafkaListener(
            topics = "${spring.kafka.events.notification-email}",
            groupId = "notify-processor-email"
    )
    public void handle(String payload) {
        sLog.info("[EMAIL-CONSUMER] Received email payload: {}", payload);
        KafkaEventMessage<EmailNotificationEvent> event = parseEvent(payload);

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

    private KafkaEventMessage<EmailNotificationEvent> parseEvent(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            JsonNode dataNode = root.path("data");
            if (dataNode.isMissingNode() || dataNode.isNull()) {
                dataNode = root.path("payload").path("data");
            }
            if (dataNode.isMissingNode() || dataNode.isNull()) {
                return null;
            }

            EmailNotificationEvent data = objectMapper.treeToValue(dataNode, EmailNotificationEvent.class);
            return KafkaEventMessage.<EmailNotificationEvent>builder()
                    .requestId(textOrNull(root.path("requestId")))
                    .requestDateTime(textOrNull(root.path("requestDateTime")))
                    .channel(textOrNull(root.path("channel")))
                    .eventId(textOrNull(root.path("eventId")))
                    .eventName(firstTextOrNull(root.path("eventName"), root.path("eventType")))
                    .aggregateId(textOrNull(root.path("aggregateId")))
                    .source(textOrNull(root.path("source")))
                    .data(data)
                    .build();
        } catch (Exception ex) {
            throw new BusinessException(ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_DATA_ERROR_MESSAGE));
        }
    }

    private String firstTextOrNull(JsonNode first, JsonNode second) {
        String value = textOrNull(first);
        return value != null ? value : textOrNull(second);
    }

    private String textOrNull(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        String value = node.asText();
        return value == null || value.isBlank() ? null : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
