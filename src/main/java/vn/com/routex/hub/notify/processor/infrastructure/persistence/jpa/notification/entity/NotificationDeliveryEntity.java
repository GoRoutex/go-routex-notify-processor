package vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.notification.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.entity.AbstractAuditingEntity;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "NOTIFICATION_DELIVERY")
public class NotificationDeliveryEntity extends AbstractAuditingEntity {

    @Id
    private String id;

    @Column(name = "NOTIFICATION_ID", nullable = false)
    private String notificationId;

    @Column(name = "PROVIDER", nullable = false)
    private String provider;

    @Column(name = "TARGET")
    private String target;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "PROVIDER_MESSAGE_ID")
    private String providerMessageId;

    @Column(name = "ERROR_CODE")
    private String errorCode;

    @Column(name = "ERROR_MESSAGE")
    private String errorMessage;

    @Column(name = "ATTEMPTED_AT")
    private OffsetDateTime attemptedAt;

}
