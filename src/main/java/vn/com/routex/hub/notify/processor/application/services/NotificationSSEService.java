package vn.com.routex.hub.notify.processor.application.services;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import vn.com.routex.hub.notify.processor.application.command.notification.GetNotificationHistoryQuery;
import vn.com.routex.hub.notify.processor.application.command.notification.GetNotificationHistoryResult;
import vn.com.routex.hub.notify.processor.application.command.notification.NotifyAllReadMarkedCommand;
import vn.com.routex.hub.notify.processor.application.command.notification.NotifyAllReadMarkedResult;
import vn.com.routex.hub.notify.processor.application.command.notification.NotifyReadMarkedCommand;
import vn.com.routex.hub.notify.processor.application.command.notification.NotifyReadMarkedResult;
import vn.com.routex.hub.notify.processor.application.command.sse.SSEStreamInformation;

public interface NotificationSSEService {
    SseEmitter streamNotification(SSEStreamInformation information);
    GetNotificationHistoryResult fetchHistory(GetNotificationHistoryQuery query);
    NotifyReadMarkedResult markAsRead(NotifyReadMarkedCommand command);
    NotifyAllReadMarkedResult markAllAsRead(NotifyAllReadMarkedCommand command);
}
