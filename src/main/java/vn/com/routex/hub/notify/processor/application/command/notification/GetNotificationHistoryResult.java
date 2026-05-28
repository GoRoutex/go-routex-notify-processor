package vn.com.routex.hub.notify.processor.application.command.notification;

import lombok.Builder;
import vn.com.routex.hub.notify.processor.domain.notification.NotificationStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Builder
public record GetNotificationHistoryResult(
        List<NotificationHistoryItem> items,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {

    @Builder
    public record NotificationHistoryItem(
        String id,
        String routeId,
        String dedupeKey,
        String driverId,
        String channel,
        String type,
        String title,
        String body,
        String deepLink,
        Map<String, Object> payload,
        NotificationStatus status,
        String merchantId,
        OffsetDateTime sentAt,
        String userEmail,
        boolean read
    ) {

    }
}
