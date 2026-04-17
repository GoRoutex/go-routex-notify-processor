package vn.com.routex.hub.notify.processor.domain.notification.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.com.routex.hub.notify.processor.domain.auditing.AbstractAuditingEntity;
import vn.com.routex.hub.notify.processor.domain.notification.NotificationStatus;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Notification extends AbstractAuditingEntity {
    private String id;
    private String routeId;
    private String dedupeKey;
    private String driverId;
    private String channel;
    private String type;
    private String title;
    private String body;
    private String deepLink;
    private Map<String, Object> payload;
    private NotificationStatus status;
    private OffsetDateTime sentAt;
}
