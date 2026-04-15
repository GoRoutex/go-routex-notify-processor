package vn.com.routex.hub.notify.processor.domain.activity.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import vn.com.routex.hub.notify.processor.domain.activity.model.RecentActivity;

public interface RecentActivityRepositoryPort {
    Page<RecentActivity> findAll(Specification<RecentActivity> spec, PageRequest page);

    void save(RecentActivity entity);
}
