package vn.com.routex.hub.notify.processor.interfaces.models.notification;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.com.routex.hub.notify.processor.interfaces.models.base.BaseRequest;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class NotifyAllDeleteRequest extends BaseRequest {
    @NotBlank
    private String merchantId;

    @NotBlank
    private String email;
}
