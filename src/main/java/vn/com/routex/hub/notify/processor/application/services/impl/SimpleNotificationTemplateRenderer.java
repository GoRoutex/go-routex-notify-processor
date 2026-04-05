package vn.com.routex.hub.notify.processor.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.routex.hub.notify.processor.application.command.notification.TemplateRendererResult;
import vn.com.routex.hub.notify.processor.application.services.NotificationTemplateRenderer;
import vn.com.routex.hub.notify.processor.domain.notification.model.NotificationTemplate;
import vn.com.routex.hub.notify.processor.domain.notification.port.NotificationTemplateRepositoryPort;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SimpleNotificationTemplateRenderer implements NotificationTemplateRenderer {

    private final NotificationTemplateRepositoryPort notificationTemplateRepositoryPort;

    @Override
    public TemplateRendererResult render(String templateCode, Map<String, Object> variables, String deeplink) {
        NotificationTemplate template = notificationTemplateRepositoryPort.findById(templateCode)
                .orElseThrow();

        String title = replace(template.getTitleTemplate(), variables);
        String body = replace(template.getBodyTemplate(), variables);

        return TemplateRendererResult
                .builder()
                .title(title)
                .body(body)
                .deeplink(deeplink)
                .build();
    }

    private String replace(String template, Map<String, Object> vars) {
        String result = template;

        for(Map.Entry<String, Object> e : vars.entrySet()) {
            result = result.replace("{{" + e.getKey() + "}}", String.valueOf(e.getValue()));
        }
        return result;
    }
}
