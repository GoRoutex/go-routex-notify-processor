package vn.com.routex.hub.notify.processor.application.routeassignment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HandleRouteAssignedEventService implements HandleRouteAssignedEventUseCase {

    @Override
    public void handle(HandleRouteAssignedEventCommand command) {
        // Application-layer entry point.
        // Keep infrastructure concerns (Kafka, JSON, headers) out of this use case.
        // Extend here to persist state, trigger notifications, or publish downstream events.
        log.info(
                "Route assigned consumed. eventId={}, aggregateId={}, routeId={}, driverId={}, vehicleId={}, assignedBy={}, assignedAt={}",
                command.eventId(),
                command.aggregateId(),
                command.payload() != null ? command.payload().routeId() : null,
                command.payload() != null ? command.payload().driverId() : null,
                command.payload() != null ? command.payload().vehicleId() : null,
                command.payload() != null ? command.payload().assignedBy() : null,
                command.payload() != null ? command.payload().assignedAt() : null
        );
    }
}

