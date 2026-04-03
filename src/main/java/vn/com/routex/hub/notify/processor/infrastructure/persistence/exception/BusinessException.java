package vn.com.routex.hub.notify.processor.infrastructure.persistence.exception;

import lombok.EqualsAndHashCode;
import vn.com.routex.hub.notify.processor.interfaces.models.result.ApiResult;

@EqualsAndHashCode(callSuper = true)
public class BusinessException extends BaseException {

    public BusinessException(String requestId, String requestDateTime, String channel, ApiResult result) {
        super(requestId, requestDateTime, channel, result);
    }

    public BusinessException(ApiResult result) { super(result); }
}
