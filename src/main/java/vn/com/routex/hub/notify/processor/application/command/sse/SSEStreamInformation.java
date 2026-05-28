package vn.com.routex.hub.notify.processor.application.command.sse;

import lombok.Builder;

@Builder
public record SSEStreamInformation(
        String merchantId,
        String email
) {
}
