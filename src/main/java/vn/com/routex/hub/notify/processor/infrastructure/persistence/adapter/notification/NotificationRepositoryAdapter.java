package vn.com.routex.hub.notify.processor.infrastructure.persistence.adapter.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import vn.com.routex.hub.notify.processor.domain.notification.model.Notification;
import vn.com.routex.hub.notify.processor.domain.notification.port.NotificationRepositoryPort;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.notification.repository.NotificationEntityRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final NotificationPersistenceMapper notificationPersistenceMapper;
    private final NotificationEntityRepository notificationEntityRepository;

    @Override
    public void save(Notification notification) {
        notificationEntityRepository.save(notificationPersistenceMapper.toEntity(notification));
    }

    @Override
    public Page<Notification> findByMerchantIdAndUserEmailOrderByCreatedAtDesc(String merchantId, String userEmail, int pageNumber, int pageSize) {
        return notificationEntityRepository
                .findByMerchantIdAndUserEmailOrderByCreatedAtDesc(merchantId, userEmail, PageRequest.of(pageNumber - 1, pageSize))
                .map(notificationPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Notification> findById(String id) {
        return notificationEntityRepository.findById(id)
                .map(notificationPersistenceMapper::toDomain);
    }

    @Override
    public List<Notification> findAll() {
        return notificationEntityRepository.findAll()
                .stream()
                .map(notificationPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Notification> findUnreadNotification(String merchantId, String userEmail) {
        return notificationEntityRepository.findUnreadNotification(merchantId, userEmail)
                .stream().map(notificationPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void saveAll(List<Notification> unreadList) {
        notificationEntityRepository.saveAll(unreadList.stream()
                .map(notificationPersistenceMapper::toEntity)
                .toList());
    }
}
