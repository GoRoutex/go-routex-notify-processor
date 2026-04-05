package vn.com.routex.hub.notify.processor.domain.seat.port;

import java.util.List;
import java.util.Map;

public interface RouteSeatAvailabilityPort {
    Map<String, Long> countAvailableSeats(List<String> routeIds);
}
