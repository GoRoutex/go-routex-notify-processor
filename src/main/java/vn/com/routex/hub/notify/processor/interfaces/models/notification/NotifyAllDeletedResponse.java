package vn.com.routex.hub.notify.processor.interfaces.models.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.com.routex.hub.notify.processor.interfaces.models.base.BaseResponse;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class NotifyAllDeletedResponse extends BaseResponse<NotifyAllDeletedResponse.NotifyAllDeletedResponseData> {

    @SuperBuilder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NotifyAllDeletedResponseData {
        private long deletedCount;
    }
}
