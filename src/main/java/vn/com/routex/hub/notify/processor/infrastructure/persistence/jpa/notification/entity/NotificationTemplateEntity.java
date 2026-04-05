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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "NOTIFICATION_TEMPLATE")
public class NotificationTemplateEntity extends AbstractAuditingEntity {

    @Id
    private String code;

    @Column(name = "TITLE_TEMPLATE", nullable = false)
    private String titleTemplate;

    @Column(name = "BODY_TEMPLATE", nullable = false)
    private String bodyTemplate;

    @Column(name = "ACTIVE")
    private Boolean active;
}
