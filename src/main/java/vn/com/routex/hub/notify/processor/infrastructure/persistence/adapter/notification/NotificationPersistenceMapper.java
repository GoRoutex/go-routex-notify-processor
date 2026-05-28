package vn.com.routex.hub.notify.processor.infrastructure.persistence.adapter.notification;


import org.springframework.stereotype.Component;
import vn.com.routex.hub.notify.processor.domain.notification.model.Notification;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.notification.entity.NotificationEntity;

@Component
public class NotificationPersistenceMapper {

    public Notification toDomain(NotificationEntity notificationEntity) {

        if(notificationEntity == null) {
            return null;
        }

        return Notification.builder()
                .id(notificationEntity.getId())
                .routeId(notificationEntity.getRouteId())
                .dedupeKey(notificationEntity.getDedupeKey())
                .driverId(notificationEntity.getDriverId())
                .channel(notificationEntity.getChannel())
                .type(notificationEntity.getType())
                .title(notificationEntity.getTitle())
                .body(notificationEntity.getBody())
                .deepLink(notificationEntity.getDeepLink())
                .payload(notificationEntity.getPayload())
                .status(notificationEntity.getStatus())
                .sentAt(notificationEntity.getSentAt())
                .merchantId(notificationEntity.getMerchantId())
                .userEmail(notificationEntity.getUserEmail())
                .read(notificationEntity.isRead())
                .createdAt(notificationEntity.getCreatedAt())
                .createdBy(notificationEntity.getCreatedBy())
                .updatedAt(notificationEntity.getUpdatedAt())
                .updatedBy(notificationEntity.getUpdatedBy())
                .build();
    }

    public NotificationEntity toEntity(Notification notification) {
        if(notification == null) {
            return null;
        }

        return NotificationEntity.builder()
                .id(notification.getId())
                .routeId(notification.getRouteId())
                .dedupeKey(notification.getDedupeKey())
                .driverId(notification.getDriverId())
                .channel(notification.getChannel())
                .title(notification.getTitle())
                .type(notification.getType())
                .body(notification.getBody())
                .deepLink(notification.getDeepLink())
                .payload(notification.getPayload())
                .status(notification.getStatus())
                .sentAt(notification.getSentAt())
                .createdAt(notification.getCreatedAt())
                .createdBy(notification.getCreatedBy())
                .updatedAt(notification.getUpdatedAt())
                .updatedBy(notification.getUpdatedBy())
                .merchantId(notification.getMerchantId())
                .userEmail(notification.getUserEmail())
                .read(notification.isRead())
                .build();
    }
}
