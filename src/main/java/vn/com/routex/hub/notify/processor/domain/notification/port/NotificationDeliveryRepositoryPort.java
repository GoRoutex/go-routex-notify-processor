package vn.com.routex.hub.notify.processor.domain.notification.port;

import vn.com.routex.hub.notify.processor.domain.notification.model.NotificationDelivery;

import java.util.List;

public interface NotificationDeliveryRepositoryPort {
    void saveAll(List<NotificationDelivery> deliveries);
}
