package vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.activity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.activity.entity.RecentActivityEntity;

public interface RecentActivityEntityRepository
        extends JpaRepository<RecentActivityEntity, String>, JpaSpecificationExecutor<RecentActivityEntity> {
}

