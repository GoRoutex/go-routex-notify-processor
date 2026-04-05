package vn.com.routex.hub.notify.processor.domain.device.model;


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
public class DeviceToken extends AbstractAuditingEntity {
    private String id;
    private String driverId;
    private String userId;
    private String deviceId;
    private String fcmToken;
    private String platform;
    private String appVersion;
    private boolean active;
    private OffsetDateTime lastSeenAt;
}
