package vn.com.routex.hub.notify.processor.application.command.common;

import lombok.Builder;

@Builder
public record RequestContext(
        String requestId,
        String requestDateTime,
        String channel,
        String merchantId
) {
}
