package vn.com.routex.hub.notify.processor.infrastructure.persistence.adapter.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.com.routex.hub.notify.processor.domain.notification.model.Notification;
import vn.com.routex.hub.notify.processor.domain.notification.port.NotificationRepositoryPort;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.notification.repository.NotificationEntityRepository;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final NotificationPersistenceMapper notificationPersistenceMapper;
    private final NotificationEntityRepository notificationEntityRepository;

    @Override
    public void save(Notification notification) {
        notificationEntityRepository.save(notificationPersistenceMapper.toEntity(notification));
    }
}
