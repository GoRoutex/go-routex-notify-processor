package vn.com.routex.hub.notify.processor.application.reminder;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import vn.com.go.routex.identity.security.log.SystemLog;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.RouteAssignedEvent;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.TicketIssuedEvent;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.model.KafkaEventMessage;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private static final long DEPARTURE_REMINDER_HOURS = 2;

    private final TaskScheduler taskScheduler;
    private final ReminderExecutor reminderExecutor;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    public void scheduleRouteReminders(RouteAssignedEvent payload) {
        if (payload == null || payload.departureTime() == null) {
            sLog.warn("[DRIVER-REMINDER] Skip scheduling because departureTime is missing. routeId={}",
                    payload != null ? payload.routeId() : null);
            return;
        }
        if (!hasText(payload.driverId())) {
            sLog.warn("[DRIVER-REMINDER] Skip scheduling because driverId is missing. routeId={}", payload.routeId());
            return;
        }

        OffsetDateTime reminderAt = payload.departureTime().minusHours(DEPARTURE_REMINDER_HOURS);
        OffsetDateTime now = OffsetDateTime.now();
        if (payload.departureTime().isBefore(now)) {
            sLog.warn("[DRIVER-REMINDER] Skip scheduling because departureTime is in the past. routeId={}, departureTime={}",
                    payload.routeId(), payload.departureTime());
            return;
        }

        ReminderJobPayload jobPayload = toPayload(payload);
        Instant executionTime = reminderAt.isAfter(now) ? reminderAt.toInstant() : Instant.now();
        taskScheduler.schedule(() -> reminderExecutor.execute(jobPayload), executionTime);

        sLog.info("[DRIVER-REMINDER] Scheduled routeId={} driverId={} reminderAt={} departureTime={}",
                payload.routeId(), payload.driverId(), executionTime, payload.departureTime());
    }

    public void scheduleTicketDepartureReminder(KafkaEventMessage<TicketIssuedEvent> event) {
        TicketIssuedEvent data = event.data();
        if (data == null || data.departureTime() == null) {
            sLog.warn("[TICKET-REMINDER] Skip scheduling because departureTime is missing. eventId={}, bookingId={}",
                    event.eventId(), data != null ? data.bookingId() : null);
            return;
        }

        OffsetDateTime reminderAt = data.departureTime().minusHours(DEPARTURE_REMINDER_HOURS);
        OffsetDateTime now = OffsetDateTime.now();
        if (data.departureTime().isBefore(now)) {
            sLog.warn("[TICKET-REMINDER] Skip scheduling because departureTime is in the past. eventId={}, bookingId={}, departureTime={}",
                    event.eventId(), data.bookingId(), data.departureTime());
            return;
        }

        ReminderJobPayload payload = toPayload(event);
        Instant executionTime = reminderAt.isAfter(now) ? reminderAt.toInstant() : Instant.now();
        taskScheduler.schedule(() -> reminderExecutor.execute(payload), executionTime);

        sLog.info("[TICKET-REMINDER] Scheduled bookingId={} customerId={} reminderAt={} departureTime={}",
                data.bookingId(), data.customerId(), executionTime, data.departureTime());
    }

    private ReminderJobPayload toPayload(KafkaEventMessage<TicketIssuedEvent> event) {
        TicketIssuedEvent data = event.data();
        List<ReminderJobPayload.TicketItem> tickets = data.tickets() == null
                ? List.of()
                : data.tickets().stream()
                .map(ticket -> ReminderJobPayload.TicketItem.builder()
                        .ticketId(ticket.ticketId())
                        .ticketCode(ticket.ticketCode())
                        .seatNumber(ticket.seatNumber())
                        .build())
                .toList();

        return ReminderJobPayload.builder()
                .type(ReminderType.TICKET_DEPARTURE_REMINDER)
                .eventId(event.eventId())
                .bookingId(data.bookingId())
                .bookingCode(data.bookingCode())
                .customerId(data.customerId())
                .customerName(data.customerName())
                .customerEmail(data.customerEmail())
                .merchantId(data.merchantId())
                .tripId(resolveTripId(data))
                .departureTime(data.departureTime())
                .totalAmount(data.totalAmount())
                .currency(data.currency())
                .tickets(tickets)
                .build();
    }

    private ReminderJobPayload toPayload(RouteAssignedEvent event) {
        return ReminderJobPayload.builder()
                .type(ReminderType.DRIVER_DEPARTURE_REMINDER)
                .tripId(event.routeId())
                .driverId(event.driverId())
                .vehicleId(event.vehicleId())
                .originName(event.originName())
                .destinationName(event.destinationName())
                .departureTime(event.departureTime())
                .build();
    }

    private String resolveTripId(TicketIssuedEvent data) {
        return hasText(data.tripId()) ? data.tripId() : data.routeId();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
