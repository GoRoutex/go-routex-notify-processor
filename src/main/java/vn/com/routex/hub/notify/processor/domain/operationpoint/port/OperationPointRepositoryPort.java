package vn.com.routex.hub.notify.processor.domain.operationpoint.port;

import vn.com.routex.hub.notify.processor.domain.common.PagedResult;
import vn.com.routex.hub.notify.processor.domain.operationpoint.model.OperationPoint;

import java.util.Optional;

public interface OperationPointRepositoryPort {
    Optional<OperationPoint> findByCode(String code);

    Optional<OperationPoint> findById(String id);

    boolean existsByCode(String code);

    void save(OperationPoint operationPoint);

    PagedResult<OperationPoint> fetch(int pageNumber, int pageSize);
}
