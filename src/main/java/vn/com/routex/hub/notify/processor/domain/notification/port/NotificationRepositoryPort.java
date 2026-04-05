package vn.com.routex.hub.notify.processor.domain.notification.port;

import vn.com.routex.hub.notify.processor.domain.notification.model.Notification;

public interface NotificationRepositoryPort {

    void save(Notification notification);
}
