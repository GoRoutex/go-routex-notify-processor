package vn.com.routex.hub.notify.processor.domain.notification.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.com.routex.hub.notify.processor.domain.auditing.AbstractAuditingEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class NotificationTemplate extends AbstractAuditingEntity {
    private String code;
    private String titleTemplate;
    private String bodyTemplate;
    private Boolean active;
}
