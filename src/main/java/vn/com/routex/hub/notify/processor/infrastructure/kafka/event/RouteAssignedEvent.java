package vn.com.routex.hub.notify.processor.infrastructure.kafka.event;

import lombok.Builder;
import vn.com.routex.hub.notify.processor.domain.route.RouteStatus;

import java.time.OffsetDateTime;

@Builder
public record RouteAssignedEvent(
        String routeId,
        String vehicleId,
        String driverId,
        String driverUserId,
        OffsetDateTime departureTime,
        String originName,
        String destinationName,
        String assignedBy,
        OffsetDateTime assignedAt,
        RouteStatus status
) {
}
