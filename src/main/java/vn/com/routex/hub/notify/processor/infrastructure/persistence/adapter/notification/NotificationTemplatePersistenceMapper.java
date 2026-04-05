package vn.com.routex.hub.notify.processor.infrastructure.persistence.adapter.notification;

import org.springframework.stereotype.Component;
import vn.com.routex.hub.notify.processor.domain.notification.model.NotificationTemplate;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.notification.entity.NotificationTemplateEntity;

@Component
public class NotificationTemplatePersistenceMapper {

    public NotificationTemplate toDomain(NotificationTemplateEntity entity) {
        if (entity == null) {
            return null;
        }

        return NotificationTemplate.builder()
                .code(entity.getCode())
                .titleTemplate(entity.getTitleTemplate())
                .bodyTemplate(entity.getBodyTemplate())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public NotificationTemplateEntity toEntity(NotificationTemplate template) {
        if (template == null) {
            return null;
        }

        return NotificationTemplateEntity.builder()
                .code(template.getCode())
                .titleTemplate(template.getTitleTemplate())
                .bodyTemplate(template.getBodyTemplate())
                .active(template.getActive())
                .createdAt(template.getCreatedAt())
                .createdBy(template.getCreatedBy())
                .updatedAt(template.getUpdatedAt())
                .updatedBy(template.getUpdatedBy())
                .build();
    }
}

