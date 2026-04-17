package vn.com.routex.hub.notify.processor.domain.notification.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.com.routex.hub.notify.processor.domain.auditing.AbstractAuditingEntity;

import java.time.OffsetDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class NotificationDelivery extends AbstractAuditingEntity {
    private String id;
    private String notificationId;
    private String provider;
    private String target;
    private String status;
    private String providerMessageId;
    private String errorCode;
    private String errorMessage;
    private OffsetDateTime attemptedAt;
}
