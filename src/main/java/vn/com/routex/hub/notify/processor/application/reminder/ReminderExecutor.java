package vn.com.routex.hub.notify.processor.application.reminder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.com.go.routex.identity.security.log.SystemLog;
import vn.com.routex.hub.notify.processor.application.services.NotificationIdempotencyService;
import vn.com.routex.hub.notify.processor.domain.notification.NotificationStatus;
import vn.com.routex.hub.notify.processor.domain.notification.model.Notification;
import vn.com.routex.hub.notify.processor.domain.notification.model.NotificationDelivery;
import vn.com.routex.hub.notify.processor.domain.notification.port.NotificationDeliveryRepositoryPort;
import vn.com.routex.hub.notify.processor.domain.notification.port.NotificationRepositoryPort;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.UserNotificationEvent;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.consumer.NotificationEventConsumer;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReminderExecutor {

    private static final String IN_APP_PROVIDER = "IN_APP";
    private static final String SYSTEM_USER = "notify-processor";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    private final NotificationIdempotencyService notificationIdempotencyService;
    private final NotificationRepositoryPort notificationRepositoryPort;
    private final NotificationDeliveryRepositoryPort notificationDeliveryRepositoryPort;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Transactional
    public void execute(ReminderJobPayload payload) {
        if (payload == null || payload.type() == null) {
            return;
        }
        if (payload.type() == ReminderType.TICKET_DEPARTURE_REMINDER) {
            executeTicketDepartureReminder(payload);
            return;
        }
        if (payload.type() == ReminderType.DRIVER_DEPARTURE_REMINDER) {
            executeDriverDepartureReminder(payload);
        }
    }

    private void executeTicketDepartureReminder(ReminderJobPayload payload) {
        String idemKey = "notif:reminder:ticket-departure:" + payload.bookingId();
        if (!notificationIdempotencyService.acquire(idemKey)) {
            sLog.warn("[TICKET-REMINDER] Skip duplicated reminder. idemKey={}", idemKey);
            return;
        }

        OffsetDateTime now = OffsetDateTime.now();
        Notification notification = buildNotification(payload, idemKey, now);
        notificationRepositoryPort.save(notification);

        NotificationDelivery inAppDelivery = buildDelivery(notification.getId(), IN_APP_PROVIDER, payload.customerId(), "SUCCESS", null, now);
        notificationDeliveryRepositoryPort.saveAll(List.of(inAppDelivery));
        sendSse(payload, notification);

        sLog.info("[TICKET-REMINDER] Sent bookingId={} customerId={} notificationId={}",
                payload.bookingId(), payload.customerId(), notification.getId());
    }

    private void executeDriverDepartureReminder(ReminderJobPayload payload) {
        String idemKey = "notif:reminder:driver-departure:" + payload.tripId() + ":" + payload.driverId();
        if (!notificationIdempotencyService.acquire(idemKey)) {
            sLog.warn("[DRIVER-REMINDER] Skip duplicated reminder. idemKey={}", idemKey);
            return;
        }

        OffsetDateTime now = OffsetDateTime.now();
        Notification notification = buildDriverNotification(payload, idemKey, now);
        notificationRepositoryPort.save(notification);

        NotificationDelivery inAppDelivery = buildDelivery(notification.getId(), IN_APP_PROVIDER, payload.driverId(), "SUCCESS", null, now);
        notificationDeliveryRepositoryPort.saveAll(List.of(inAppDelivery));

        sLog.info("[DRIVER-REMINDER] Sent tripId={} driverId={} notificationId={}",
                payload.tripId(), payload.driverId(), notification.getId());
    }

    private Notification buildNotification(ReminderJobPayload payload, String idemKey, OffsetDateTime now) {
        return Notification.builder()
                .id(UUID.randomUUID().toString())
                .routeId(payload.tripId())
                .dedupeKey(idemKey)
                .driverId(payload.customerId())
                .channel(IN_APP_PROVIDER)
                .type(ReminderType.TICKET_DEPARTURE_REMINDER.name())
                .title("Sap den gio khoi hanh")
                .body(buildBody(payload))
                .deepLink("routexgo://tickets/" + payload.bookingId())
                .payload(buildPayload(payload))
                .status(NotificationStatus.SENT)
                .sentAt(now)
                .merchantId(payload.merchantId())
                .userEmail(payload.customerEmail())
                .read(false)
                .createdAt(now)
                .createdBy(SYSTEM_USER)
                .updatedAt(now)
                .updatedBy(SYSTEM_USER)
                .build();
    }

    private Notification buildDriverNotification(ReminderJobPayload payload, String idemKey, OffsetDateTime now) {
        return Notification.builder()
                .id(UUID.randomUUID().toString())
                .routeId(payload.tripId())
                .dedupeKey(idemKey)
                .driverId(payload.driverId())
                .channel(IN_APP_PROVIDER)
                .type(ReminderType.DRIVER_DEPARTURE_REMINDER.name())
                .title("Sap den gio khoi hanh")
                .body(buildDriverBody(payload))
                .deepLink("routexgo://trip/" + payload.tripId())
                .payload(buildDriverPayload(payload))
                .status(NotificationStatus.SENT)
                .sentAt(now)
                .read(false)
                .createdAt(now)
                .createdBy(SYSTEM_USER)
                .updatedAt(now)
                .updatedBy(SYSTEM_USER)
                .build();
    }

    private void sendSse(ReminderJobPayload payload, Notification notification) {
        if (!hasText(payload.merchantId()) || !hasText(payload.customerEmail())) {
            return;
        }

        UserNotificationEvent event = UserNotificationEvent.builder()
                .merchantId(payload.merchantId())
                .userEmail(payload.customerEmail())
                .title(notification.getTitle())
                .message(notification.getBody())
                .notificationType(notification.getType())
                .referenceId(notification.getDeepLink())
                .build();
        NotificationEventConsumer.sendNotification(payload.merchantId(), payload.customerEmail(), event);
    }

    private NotificationDelivery buildDelivery(String notificationId,
                                               String provider,
                                               String target,
                                               String status,
                                               String errorMessage,
                                               OffsetDateTime now) {
        return NotificationDelivery.builder()
                .id(UUID.randomUUID().toString())
                .notificationId(notificationId)
                .provider(provider)
                .target(target)
                .status(status)
                .attemptedAt(now)
                .errorMessage(errorMessage)
                .createdAt(now)
                .createdBy(SYSTEM_USER)
                .updatedAt(now)
                .updatedBy(SYSTEM_USER)
                .build();
    }

    private String buildBody(ReminderJobPayload payload) {
        String seats = payload.tickets() == null ? "" : payload.tickets().stream()
                .map(ReminderJobPayload.TicketItem::seatNumber)
                .filter(this::hasText)
                .collect(Collectors.joining(", "));
        String departure = payload.departureTime() != null ? payload.departureTime().format(TIME_FORMATTER) : "";
        return "Chuyen xe cua ban se khoi hanh luc " + departure + ". Ma dat cho: " + payload.bookingCode()
                + (seats.isBlank() ? "" : ". Ghe: " + seats);
    }

    private String buildDriverBody(ReminderJobPayload payload) {
        String departure = payload.departureTime() != null ? payload.departureTime().format(TIME_FORMATTER) : "";
        String route = buildRouteLabel(payload);
        return "Chuyen xe" + route + " se khoi hanh luc " + departure + ". Vui long kiem tra manifest va san sang don khach.";
    }

    private String buildRouteLabel(ReminderJobPayload payload) {
        if (hasText(payload.originName()) && hasText(payload.destinationName())) {
            return " " + payload.originName() + " - " + payload.destinationName();
        }
        return "";
    }

    private Map<String, Object> buildPayload(ReminderJobPayload payload) {
        Map<String, Object> data = new HashMap<>();
        data.put("bookingId", payload.bookingId());
        data.put("bookingCode", payload.bookingCode());
        data.put("customerId", payload.customerId());
        data.put("merchantId", payload.merchantId());
        data.put("tripId", payload.tripId());
        data.put("departureTime", payload.departureTime());
        data.put("tickets", payload.tickets());
        return data;
    }

    private Map<String, Object> buildDriverPayload(ReminderJobPayload payload) {
        Map<String, Object> data = new HashMap<>();
        data.put("tripId", payload.tripId());
        data.put("driverId", payload.driverId());
        data.put("vehicleId", payload.vehicleId());
        data.put("originName", payload.originName());
        data.put("destinationName", payload.destinationName());
        data.put("departureTime", payload.departureTime());
        return data;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
