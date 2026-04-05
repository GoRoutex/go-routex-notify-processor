package vn.com.routex.hub.notify.processor.application.services;

import vn.com.routex.hub.notify.processor.application.command.notification.TemplateRendererResult;

import java.util.Map;

public interface NotificationTemplateRenderer {
    TemplateRendererResult render(String templateCode, Map<String, Object> variables, String deeplink);
}
