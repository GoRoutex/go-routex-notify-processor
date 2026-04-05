package vn.com.routex.hub.notify.processor.infrastructure.persistence.adapter.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.com.routex.hub.notify.processor.domain.notification.model.NotificationTemplate;
import vn.com.routex.hub.notify.processor.domain.notification.port.NotificationTemplateRepositoryPort;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.notification.repository.NotificationTemplateRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NotificationTemplateRepositoryAdapter implements NotificationTemplateRepositoryPort {

    private final NotificationTemplatePersistenceMapper notificationTemplatePersistenceMapper;
    private final NotificationTemplateRepository notificationTemplateRepository;

    @Override
    public Optional<NotificationTemplate> findById(String templateCode) {
        return notificationTemplateRepository.findById(templateCode)
                .map(notificationTemplatePersistenceMapper::toDomain);
    }
}
