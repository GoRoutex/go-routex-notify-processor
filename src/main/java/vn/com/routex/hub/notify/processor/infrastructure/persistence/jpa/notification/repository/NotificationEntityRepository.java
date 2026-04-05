package vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.notification.entity.NotificationEntity;

@Repository
public interface NotificationEntityRepository extends JpaRepository<NotificationEntity, String> {
}
