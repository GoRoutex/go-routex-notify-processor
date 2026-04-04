package vn.com.routex.hub.notify.processor.application.routeassignment;

import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.RouteAssignedEvent;

import java.time.OffsetDateTime;

public record HandleRouteAssignedEventCommand(
        String requestId,
        String requestDateTime,
        String channel,
        String eventId,
        String aggregateId,
        OffsetDateTime occurredAt,
        RouteAssignedEvent payload
) {
}

