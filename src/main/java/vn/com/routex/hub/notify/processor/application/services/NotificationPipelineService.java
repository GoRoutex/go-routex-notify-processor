package vn.com.routex.hub.notify.processor.application.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import vn.com.routex.hub.notify.processor.application.command.notification.NotificationCommand;
import vn.com.routex.hub.notify.processor.application.command.notification.NotificationResult;

public interface NotificationPipelineService {
    NotificationResult process(NotificationCommand notificationCommand) throws JsonProcessingException;
}
