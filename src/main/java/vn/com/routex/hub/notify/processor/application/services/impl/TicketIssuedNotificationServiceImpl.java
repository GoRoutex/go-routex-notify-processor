package vn.com.routex.hub.notify.processor.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.go.routex.identity.security.log.SystemLog;
import vn.com.routex.hub.notify.processor.application.services.NotificationIdempotencyService;
import vn.com.routex.hub.notify.processor.application.services.TicketIssuedNotificationService;
import vn.com.routex.hub.notify.processor.domain.notification.NotificationStatus;
import vn.com.routex.hub.notify.processor.domain.notification.model.Notification;
import vn.com.routex.hub.notify.processor.domain.notification.model.NotificationDelivery;
import vn.com.routex.hub.notify.processor.domain.notification.port.NotificationDeliveryRepositoryPort;
import vn.com.routex.hub.notify.processor.domain.notification.port.NotificationRepositoryPort;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.TicketIssuedEvent;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.model.KafkaEventMessage;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketIssuedNotificationServiceImpl implements TicketIssuedNotificationService {

    private static final String NOTIFICATION_TYPE = "TICKET_ISSUED";
    private static final String IN_APP_PROVIDER = "IN_APP";
    private static final String EMAIL_PROVIDER = "EMAIL";
    private static final String SMS_PROVIDER = "SMS";

    private final NotificationIdempotencyService notificationIdempotencyService;
    private final NotificationRepositoryPort notificationRepositoryPort;
    private final NotificationDeliveryRepositoryPort notificationDeliveryRepositoryPort;

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    public void process(KafkaEventMessage<TicketIssuedEvent> event) {
        String idemKey = "notif:event:" + event.eventName() + ":" + event.aggregateId();
        if (!notificationIdempotencyService.acquire(idemKey)) {
            sLog.warn("[TICKET-ISSUED] Skip duplicated event: {}", idemKey);
            return;
        }

        TicketIssuedEvent data = event.data();
        OffsetDateTime now = OffsetDateTime.now();
        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .routeId(data.routeId())
                .dedupeKey(idemKey)
                .driverId(data.customerId())
                .channel(IN_APP_PROVIDER)
                .type(NOTIFICATION_TYPE)
                .title(buildTitle(data))
                .body(buildBody(data))
                .deepLink("routexgo://tickets/" + data.bookingId())
                .payload(buildPayload(data))
                .status(NotificationStatus.SENT)
                .sentAt(now)
                .createdAt(now)
                .createdBy("notify-processor")
                .updatedAt(now)
                .updatedBy("notify-processor")
                .build();

        notificationRepositoryPort.save(notification);

        List<NotificationDelivery> deliveries = List.of(
                buildDelivery(notification.getId(), IN_APP_PROVIDER, data.customerId(), now, "SUCCESS", null),
                buildDelivery(notification.getId(), EMAIL_PROVIDER, data.customerEmail(), now,
                        hasText(data.customerEmail()) ? "SUCCESS" : "SKIPPED",
                        hasText(data.customerEmail()) ? null : "Customer email is missing"),
                buildDelivery(notification.getId(), SMS_PROVIDER, data.customerPhone(), now,
                        hasText(data.customerPhone()) ? "SUCCESS" : "SKIPPED",
                        hasText(data.customerPhone()) ? null : "Customer phone is missing")
        );

        notificationDeliveryRepositoryPort.saveAll(deliveries);

        sLog.info("[TICKET-ISSUED] In-app notification sent for bookingId={} customerId={}", data.bookingId(), data.customerId());
        if (hasText(data.customerEmail())) {
            sLog.info("[TICKET-ISSUED] Email notification sent to {}", data.customerEmail());
        }
        if (hasText(data.customerPhone())) {
            sLog.info("[TICKET-ISSUED] SMS notification sent to {}", data.customerPhone());
        }
    }

    private String buildTitle(TicketIssuedEvent data) {
        return "Ve cua ban da duoc phat hanh";
    }

    private String buildBody(TicketIssuedEvent data) {
        String seats = data.tickets().stream()
                .map(TicketIssuedEvent.TicketIssuedItem::seatNumber)
                .collect(Collectors.joining(", "));
        return "Dat cho " + data.bookingCode() + " da thanh toan thanh cong. Ghe: " + seats;
    }

    private Map<String, Object> buildPayload(TicketIssuedEvent data) {
        return Map.of(
                "bookingId", data.bookingId(),
                "bookingCode", data.bookingCode(),
                "customerId", data.customerId(),
                "routeId", data.routeId(),
                "currency", data.currency(),
                "totalAmount", data.totalAmount(),
                "tickets", data.tickets()
        );
    }

    private NotificationDelivery buildDelivery(String notificationId,
                                               String provider,
                                               String target,
                                               OffsetDateTime attemptedAt,
                                               String status,
                                               String errorMessage) {
        return NotificationDelivery.builder()
                .id(UUID.randomUUID().toString())
                .notificationId(notificationId)
                .provider(provider)
                .target(target)
                .status(status)
                .attemptedAt(attemptedAt)
                .errorMessage(errorMessage)
                .createdAt(attemptedAt)
                .createdBy("notify-processor")
                .updatedAt(attemptedAt)
                .updatedBy("notify-processor")
                .build();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
