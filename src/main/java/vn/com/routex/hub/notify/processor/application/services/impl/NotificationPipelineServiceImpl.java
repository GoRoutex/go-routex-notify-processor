package vn.com.routex.hub.notify.processor.application.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.com.routex.hub.notify.processor.application.command.notification.NotificationCommand;
import vn.com.routex.hub.notify.processor.application.command.notification.NotificationResult;
import vn.com.routex.hub.notify.processor.application.command.notification.TemplateRendererResult;
import vn.com.routex.hub.notify.processor.application.services.NotificationPipelineService;
import vn.com.routex.hub.notify.processor.application.services.NotificationTemplateRenderer;
import vn.com.routex.hub.notify.processor.domain.notification.NotificationStatus;
import vn.com.routex.hub.notify.processor.domain.notification.model.Notification;
import vn.com.routex.hub.notify.processor.domain.notification.port.NotificationRepositoryPort;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationPipelineServiceImpl implements NotificationPipelineService {

    private final NotificationTemplateRenderer notificationTemplateRenderer;
    private final NotificationRepositoryPort notificationRepositoryPort;


    @Override
    public NotificationResult process(NotificationCommand notificationCommand) throws JsonProcessingException {
        TemplateRendererResult result = notificationTemplateRenderer.render(
                notificationCommand.templateCode(),
                merge(notificationCommand.titleVariables(), notificationCommand.bodyVariables(), notificationCommand.metadata()),
                notificationCommand.deepLink()
        );


        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .routeId(notificationCommand.routeId())
                .driverId(notificationCommand.driverId())
                .type(notificationCommand.type())
                .title(result.title())
                .body(result.body())
                .deepLink(result.deeplink())
                .payload(notificationCommand.metadata())
                .channel("IN_APP")
                .status(NotificationStatus.SENT)
                .sentAt(OffsetDateTime.now())
                .createdAt(OffsetDateTime.now())
                .build();

        notificationRepositoryPort.save(notification);

        return new NotificationResult();
    }


    private Map<String, Object> merge(Map<String, Object> a, Map<String, Object> b, Map<String, Object> c) {
        Map<String, Object> result = new HashMap<>();

        if(a != null) result.putAll(a);
        if(b != null) result.putAll(b);
        if(c != null) result.putAll(c);

        return result;
    }
}
