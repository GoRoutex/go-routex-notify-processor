package vn.com.routex.hub.notify.processor.application.command.notification;

import lombok.Builder;

import java.util.List;

@Builder
public record NotifyAllReadMarkedResult(
        List<NotifyReadMarkedResult> item
) {
}
