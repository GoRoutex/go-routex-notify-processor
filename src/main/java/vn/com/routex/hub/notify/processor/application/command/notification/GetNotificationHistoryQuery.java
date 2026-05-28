package vn.com.routex.hub.notify.processor.application.command.notification;

import lombok.Builder;
import vn.com.routex.hub.notify.processor.application.command.common.RequestContext;

@Builder
public record GetNotificationHistoryQuery(
        RequestContext context,
        String merchantId,
        String email,
        int pageNumber,
        int pageSize
) {
}
