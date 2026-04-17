package vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.device.entity;

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
@Table(name = "DEVICE_TOKEN")
public class DeviceTokenEntity extends AbstractAuditingEntity {

    @Id
    private String id;

    @Column(name = "DRIVER_ID", nullable = false)
    private String driverId;

    @Column(name = "USER_ID", nullable = false)
    private String userId;

    @Column(name = "DEVICE_ID", nullable = false)
    private String deviceId;

    @Column(name = "FCM_TOKEN", nullable = false)
    private String fcmToken;

    @Column(name = "PLATFORM", nullable = false)
    private String platForm;

    @Column(name = "APP_VERSION")
    private String appVersion;

    @Column(name = "ACTIVE")
    private boolean active;

    @Column(name = "LAST_SEEN_AT")
    private OffsetDateTime lastSeenAt;

}
