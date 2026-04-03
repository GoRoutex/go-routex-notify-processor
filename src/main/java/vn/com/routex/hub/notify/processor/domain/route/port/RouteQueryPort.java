package vn.com.routex.hub.notify.processor.domain.route.port;


import vn.com.routex.hub.notify.processor.domain.common.PagedResult;
import vn.com.routex.hub.notify.processor.domain.route.readmodel.RouteFetchView;
import vn.com.routex.hub.notify.processor.domain.route.readmodel.RouteSearchView;

import java.time.OffsetDateTime;
import java.util.List;

public interface RouteQueryPort {
    List<RouteSearchView> searchAssignedRoutes(
            String origin,
            String destination,
            OffsetDateTime startTime,
            OffsetDateTime endTime,
            int pageNumber,
            int pageSize
    );

    PagedResult<RouteFetchView> fetchRoutes(int pageNumber, int pageSize);
}
