package vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.notification.entity.NotificationEntity;

import java.util.List;

@Repository
public interface NotificationEntityRepository extends JpaRepository<NotificationEntity, String> {
    Page<NotificationEntity> findByMerchantIdAndUserEmailOrderByCreatedAtDesc(String merchantId, String userEmail, Pageable pageable);

    @Query(
        value = """
            SELECT n FROM NotificationEntity n
                   WHERE n.merchantId = :merchantId
                   AND n.userEmail = :userEmail
                   AND n.read is false
            """
    )
    List<NotificationEntity> findUnreadNotification(
            @Param("merchantId") String merchantId,
            @Param("userEmail") String userEmail);

    long deleteByMerchantIdAndUserEmail(String merchantId, String userEmail);
}
