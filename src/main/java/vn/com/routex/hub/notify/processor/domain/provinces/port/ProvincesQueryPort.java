package vn.com.routex.hub.notify.processor.domain.provinces.port;


import vn.com.routex.hub.notify.processor.domain.common.PagedResult;
import vn.com.routex.hub.notify.processor.domain.provinces.readmodel.ProvincesFetchView;
import vn.com.routex.hub.notify.processor.domain.provinces.readmodel.ProvincesSearchItem;

import java.util.List;

public interface ProvincesQueryPort {
    List<ProvincesSearchItem> search(String keyword, int page, int size);

    PagedResult<ProvincesFetchView> fetchRoutes(int pageNumber, int pageSize);
}
