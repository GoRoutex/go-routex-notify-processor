package vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.route.projection;

public interface RouteSeatAvailabilityProjection {

    String getRouteId();

    Long getAvailableSeat();
}

