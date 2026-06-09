package vn.com.routex.hub.notify.processor.application.command.notification;

import lombok.Builder;
import vn.com.routex.hub.notify.processor.application.command.common.RequestContext;

@Builder
public record NotifyDeletedCommand(
        RequestContext context,
        String id
) {
}
