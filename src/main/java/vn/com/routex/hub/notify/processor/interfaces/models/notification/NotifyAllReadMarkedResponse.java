package vn.com.routex.hub.notify.processor.interfaces.models.notification;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.com.routex.hub.notify.processor.interfaces.models.base.BaseResponse;

import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class NotifyAllReadMarkedResponse extends BaseResponse<List<NotifyReadMarkedResponse.NotifyReadMarkedResponseData>> {
}
