package vn.com.routex.hub.notify.processor.domain.route.port;


import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.RouteSellableEvent;

public interface RouteSaleEventPort {
    void publishRouteReadyForSale(
            String requestId,
            String requestDateTime,
            String channel,
            String aggregateId,
            RouteSellableEvent payload
    );
}
