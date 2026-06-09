package vn.com.routex.hub.notify.processor.application.command.notification;

import lombok.Builder;

@Builder
public record NotifyDeletedResult(
        String id,
        boolean deleted
) {
}
