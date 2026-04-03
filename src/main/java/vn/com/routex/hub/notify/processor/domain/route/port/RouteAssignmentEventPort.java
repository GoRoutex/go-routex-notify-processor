package vn.com.routex.hub.notify.processor.domain.route.port;


import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.RouteAssignedEvent;

public interface RouteAssignmentEventPort {
    void publishAssignedRoute(
            String requestId,
            String requestDateTime,
            String channel,
            String routeId,
            RouteAssignedEvent event
    );
}
