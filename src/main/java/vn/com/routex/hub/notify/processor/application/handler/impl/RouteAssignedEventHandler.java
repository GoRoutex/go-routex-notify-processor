package vn.com.routex.hub.notify.processor.application.handler.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.com.go.routex.identity.security.log.SystemLog;
import vn.com.routex.hub.notify.processor.application.command.notification.NotificationCommand;
import vn.com.routex.hub.notify.processor.application.handler.OutBoxEventHandler;
import vn.com.routex.hub.notify.processor.application.reminder.ReminderScheduler;
import vn.com.routex.hub.notify.processor.application.services.NotificationIdempotencyService;
import vn.com.routex.hub.notify.processor.application.services.NotificationPipelineService;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.DomainEvent;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.RouteAssignedEvent;

import java.util.Map;


@Component
@RequiredArgsConstructor
public class RouteAssignedEventHandler implements OutBoxEventHandler {

    @Value("${spring.kafka.events.route-assigned}")
    private String routeAssignedEvent;

    private final NotificationIdempotencyService notificationIdempotencyService;
    private final NotificationPipelineService notificationPipelineService;
    private final ReminderScheduler reminderScheduler;

    private final ObjectMapper objectMapper;

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    public String getEventType() {
        return routeAssignedEvent;
    }

    @Override
    public void eventHandler(DomainEvent event) {

        String idemKey = "notif:event" + event.eventType() + event.aggregateId();

        if(!notificationIdempotencyService.acquire(idemKey)) {
            sLog.warn("Skip duplicated event: {}", idemKey);
            return;
        }
        RouteAssignedEvent assignedEvent = objectMapper.convertValue(event.payload().get("data"), RouteAssignedEvent.class);

        NotificationCommand command = NotificationCommand
                .builder()
                .routeId(assignedEvent.routeId())
                .driverId(assignedEvent.driverId())
                .vehicleId(assignedEvent.vehicleId())
                .driverUserId(assignedEvent.driverUserId())
                .type("ROUTE_ASSIGNED")
                .titleVariables(Map.of(
                        "originName", assignedEvent.originName(),
                        "destinationName", assignedEvent.destinationName()
                ))
                .bodyVariables(Map.of(
                        "departureTime", assignedEvent.departureTime(),
                        "assignedAt", assignedEvent.assignedAt(),
                        "status", assignedEvent.status()
                ))
                .deepLink("routexgo://trip/" + assignedEvent.routeId())
                .metadata(Map.of(
                        "routeId", assignedEvent.routeId()
                ))
                .build();

        sLog.info("[NOTIFY] Notification Command: {}", command);

        try {
            notificationPipelineService.process(command);
        } catch (Exception e) {
            sLog.warn("[NOTIFY] Failed to process notification command: {}", e.getMessage());
        }

        reminderScheduler.scheduleRouteReminders(assignedEvent);

    }
}
