package vn.com.routex.hub.notify.processor.infrastructure.kafka.event;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record RouteOpenForBookingEvent(
        String routeId,
        String vehicleId,
        Integer seatCount,
        String creator,
        OffsetDateTime assignedAt
) {
}
