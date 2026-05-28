package vn.com.routex.hub.notify.processor.domain.department.port;

import vn.com.routex.hub.notify.processor.domain.common.PagedResult;
import vn.com.routex.hub.notify.processor.domain.department.model.Department;

import java.util.Optional;

public interface DepartmentRepositoryPort {

    Optional<Department> findById(String id);

    void save(Department department);

    PagedResult<Department> fetch(int pageNumber, int pageSize);
}
