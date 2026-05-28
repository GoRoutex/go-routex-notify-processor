package vn.com.routex.hub.notify.processor.domain.notification.port;

import org.springframework.data.domain.Page;
import vn.com.routex.hub.notify.processor.domain.notification.model.Notification;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface NotificationRepositoryPort {
    void save(Notification notification);

    Page<Notification> findByMerchantIdAndUserEmailOrderByCreatedAtDesc(String merchantId, String userEmail, int pageNumber, int pageSize);

    Optional<Notification> findById(String id);

    List<Notification> findAll();

    List<Notification> findUnreadNotification(String merchantId, String userEmail);

    void saveAll(List<Notification> unreadList);
}
