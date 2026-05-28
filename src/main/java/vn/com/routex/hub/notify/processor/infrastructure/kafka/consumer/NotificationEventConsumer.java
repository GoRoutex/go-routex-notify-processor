package vn.com.routex.hub.notify.processor.infrastructure.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import vn.com.go.routex.identity.security.log.SystemLog;
import vn.com.routex.hub.notify.processor.domain.notification.NotificationStatus;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.DomainEvent;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.UserNotificationEvent;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.notification.entity.NotificationEntity;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.notification.repository.NotificationEntityRepository;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.utils.JsonUtils;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import static vn.com.routex.hub.notify.processor.interfaces.controller.SseNotificationController.emitters;

@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());
    private final NotificationEntityRepository notificationRepository;

    @KafkaListener(
            topics = "routex.notification.user",
            groupId = "notify-processor-user-notifications"
    )
    public void handle(String payload) {
        sLog.info("[USER-NOTIFY-CONSUMER] Received user notification payload: {}", payload);
        
        DomainEvent event;
        try {
            event = JsonUtils.parseToObject(payload, DomainEvent.class);
        } catch (Exception e) {
            sLog.error("[USER-NOTIFY-CONSUMER] Failed to parse payload to DomainEvent: {}", e.getMessage());
            return;
        }

        if (event == null || event.payload() == null) {
            sLog.warn("[USER-NOTIFY-CONSUMER] Parsed event or payload is null.");
            return;
        }

        // Extract "data" block inside payload
        Map<String, Object> dataMap = (Map<String, Object>) event.payload().get("data");
        if (dataMap == null) {
            sLog.warn("[USER-NOTIFY-CONSUMER] Event data payload block is missing.");
            return;
        }

        String merchantId = (String) dataMap.get("merchantId");
        String userEmail = (String) dataMap.get("userEmail");
        String title = (String) dataMap.get("title");
        String message = (String) dataMap.get("message");
        String notificationType = (String) dataMap.get("notificationType");
        String referenceId = (String) dataMap.get("referenceId");

        sLog.info("[USER-NOTIFY-CONSUMER] Saving and dispatching SSE notification to merchantId={}, email={}", 
                merchantId, userEmail);
        
        try {
            notificationRepository.save(NotificationEntity.builder()
                    .id(UUID.randomUUID().toString())
                    .merchantId(merchantId)
                    .userEmail(userEmail)
                    .title(title)
                    .body(message)
                    .type(notificationType)
                    .deepLink(referenceId)
                    .status(NotificationStatus.SENT)
                    .sentAt(OffsetDateTime.now())
                    .read(false)
                    .build());
        } catch (Exception e) {
            sLog.error("[USER-NOTIFY-CONSUMER] Failed to save notification to database", e);
        }
        
        // Build a matching event object to pass to SSE emitter
        UserNotificationEvent data = UserNotificationEvent.builder()
                .merchantId(merchantId)
                .userEmail(userEmail)
                .title(title)
                .message(message)
                .notificationType(notificationType)
                .referenceId(referenceId)
                .build();

        sendNotification(merchantId, userEmail, data);
    }

    public static void sendNotification(String merchantId, String email, Object payload) {
        Map<String, SseEmitter> merchantEmitters = emitters.get(merchantId);
        if (merchantEmitters != null) {
            SseEmitter emitter = merchantEmitters.get(email);
            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("NOTIFICATION")
                            .data(payload, MediaType.APPLICATION_JSON));
                } catch (IOException e) {
                    merchantEmitters.remove(email);
                }
            }
        }
    }
}
