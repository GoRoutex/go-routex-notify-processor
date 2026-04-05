package vn.com.routex.hub.notify.processor.domain.notification.port;

import vn.com.routex.hub.notify.processor.domain.notification.model.NotificationTemplate;

import java.util.Optional;

public interface NotificationTemplateRepositoryPort {
    Optional<NotificationTemplate> findById(String templateCode);
}
