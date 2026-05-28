package vn.com.routex.hub.notify.processor.interfaces.models.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.com.routex.hub.notify.processor.domain.notification.NotificationStatus;
import vn.com.routex.hub.notify.processor.interfaces.models.base.BaseResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class GetNotificationHistoryResponse extends BaseResponse<List<GetNotificationHistoryResponse.GetNotificationHistoryResponseData>> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class GetNotificationHistoryResponseData {
        private String id;
        private String routeId;
        private String dedupeKey;
        private String driverId;
        private String channel;
        private String type;
        private String title;
        private String body;
        private String deepLink;
        private Map<String, Object> payload;
        private NotificationStatus status;
        private String merchantId;
        private OffsetDateTime sentAt;
        private String userEmail;
        private boolean read;
    }
}
