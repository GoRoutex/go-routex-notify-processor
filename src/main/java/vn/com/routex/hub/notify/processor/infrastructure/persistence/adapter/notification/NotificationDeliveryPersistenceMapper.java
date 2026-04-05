package vn.com.routex.hub.notify.processor.infrastructure.persistence.adapter.notification;


import org.springframework.stereotype.Component;
import vn.com.routex.hub.notify.processor.domain.notification.model.NotificationDelivery;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.notification.entity.NotificationDeliveryEntity;

@Component
public class NotificationDeliveryPersistenceMapper {
    public NotificationDeliveryEntity toEntity(NotificationDelivery delivery) {
        if(delivery == null) {
            return null;
        }

        return NotificationDeliveryEntity.builder()
                .id(delivery.getId())
                .notificationId(delivery.getNotificationId())
                .provider(delivery.getProvider())
                .target(delivery.getTarget())
                .status(delivery.getStatus())
                .providerMessageId(delivery.getProviderMessageId())
                .errorCode(delivery.getErrorCode())
                .errorMessage(delivery.getErrorMessage())
                .attemptedAt(delivery.getAttemptedAt())
                .build();
    }

    public NotificationDelivery toDomain(NotificationDeliveryEntity entity) {
        if(entity == null) {
            return null;
        }

        return NotificationDelivery.builder()
                .id(entity.getId())
                .notificationId(entity.getNotificationId())
                .provider(entity.getProvider())
                .target(entity.getTarget())
                .status(entity.getStatus())
                .providerMessageId(entity.getProviderMessageId())
                .errorCode(entity.getErrorCode())
                .errorMessage(entity.getErrorMessage())
                .attemptedAt(entity.getAttemptedAt())
                .build();
    }
}
