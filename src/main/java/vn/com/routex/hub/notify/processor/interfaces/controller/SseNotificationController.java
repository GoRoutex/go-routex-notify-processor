package vn.com.routex.hub.notify.processor.interfaces.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import vn.com.routex.hub.notify.processor.application.command.notification.GetNotificationHistoryQuery;
import vn.com.routex.hub.notify.processor.application.command.notification.GetNotificationHistoryResult;
import vn.com.routex.hub.notify.processor.application.command.notification.NotifyAllReadMarkedCommand;
import vn.com.routex.hub.notify.processor.application.command.notification.NotifyAllReadMarkedResult;
import vn.com.routex.hub.notify.processor.application.command.notification.NotifyReadMarkedCommand;
import vn.com.routex.hub.notify.processor.application.command.notification.NotifyReadMarkedResult;
import vn.com.routex.hub.notify.processor.application.command.sse.SSEStreamInformation;
import vn.com.routex.hub.notify.processor.application.services.NotificationSSEService;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.utils.ApiRequestUtils;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.utils.HttpUtils;
import vn.com.routex.hub.notify.processor.interfaces.models.base.BaseRequest;
import vn.com.routex.hub.notify.processor.interfaces.models.notification.GetNotificationHistoryResponse;
import vn.com.routex.hub.notify.processor.interfaces.models.notification.NotifyAllReadMarkedResponse;
import vn.com.routex.hub.notify.processor.interfaces.models.notification.NotifyReadMarkedResponse;
import vn.com.routex.hub.notify.processor.interfaces.models.result.ApiResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static vn.com.routex.hub.notify.processor.infrastructure.persistence.constant.ApiConstant.API_PATH;
import static vn.com.routex.hub.notify.processor.infrastructure.persistence.constant.ApiConstant.API_VERSION;
import static vn.com.routex.hub.notify.processor.infrastructure.persistence.constant.ApiConstant.NOTIFICATION_PATH;
import static vn.com.routex.hub.notify.processor.infrastructure.persistence.constant.ApiConstant.READ_ALL_PATH;
import static vn.com.routex.hub.notify.processor.infrastructure.persistence.constant.ApiConstant.READ_PATH;
import static vn.com.routex.hub.notify.processor.infrastructure.persistence.constant.ApiConstant.STREAM_PATH;

@RestController
@RequestMapping( API_PATH + API_VERSION + NOTIFICATION_PATH)
@RequiredArgsConstructor
public class SseNotificationController {
    private final NotificationSSEService notificationSSEService;

    // Key: merchantId, Value: Map of userEmail -> SseEmitter
    public static final Map<String, Map<String, SseEmitter>> emitters = new ConcurrentHashMap<>();

    @GetMapping(value = STREAM_PATH, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@RequestParam String merchantId, @RequestParam String email) {
        return notificationSSEService.streamNotification(SSEStreamInformation.builder()
                .email(email)
                .merchantId(merchantId)
                .build());
    }

    @GetMapping
    public ResponseEntity<GetNotificationHistoryResponse> getNotificationHistory(
            HttpServletRequest servletRequest,
            @RequestParam String merchantId,
            @RequestParam String email,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        BaseRequest request = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        GetNotificationHistoryResult result = notificationSSEService.fetchHistory(GetNotificationHistoryQuery.builder()
                .merchantId(merchantId)
                .email(email)
                .pageNumber(page)
                .pageSize(size)
                .build());

        GetNotificationHistoryResponse response = GetNotificationHistoryResponse.builder()
                .data(result.items()
                        .stream()
                        .map(item -> GetNotificationHistoryResponse.GetNotificationHistoryResponseData.builder()
                                .id(item.id())
                                .routeId(item.routeId())
                                .dedupeKey(item.dedupeKey())
                                .driverId(item.driverId())
                                .channel(item.channel())
                                .type(item.type())
                                .title(item.title())
                                .body(item.body())
                                .deepLink(item.deepLink())
                                .payload(item.payload())
                                .status(item.status())
                                .merchantId(item.merchantId())
                                .sentAt(item.sentAt())
                                .userEmail(item.userEmail())
                                .read(item.read())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(READ_PATH)
    public ResponseEntity<NotifyReadMarkedResponse> markAsRead(@RequestParam String id, HttpServletRequest servletRequest) {
        BaseRequest request = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);

        NotifyReadMarkedResult result = notificationSSEService.markAsRead(NotifyReadMarkedCommand.builder()
                .id(id)
                .build());

        NotifyReadMarkedResponse response = NotifyReadMarkedResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(ApiResult.buildSuccess())
                .data(NotifyReadMarkedResponse.NotifyReadMarkedResponseData.builder()
                        .id(result.id())
                        .read(result.read())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(READ_ALL_PATH)
    public ResponseEntity<NotifyAllReadMarkedResponse> markAllAsRead(
            HttpServletRequest servletRequest,
            @RequestParam String merchantId,
            @RequestParam String email) {

        BaseRequest request = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        NotifyAllReadMarkedResult result = notificationSSEService.markAllAsRead(NotifyAllReadMarkedCommand.builder()
                .context(HttpUtils.toContext(request))
                .merchantId(merchantId)
                .email(email).build());

        NotifyAllReadMarkedResponse response = NotifyAllReadMarkedResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(ApiResult.buildSuccess())
                .data(result.item().stream()
                        .map(item -> NotifyReadMarkedResponse.NotifyReadMarkedResponseData.builder()
                                .id(item.id())
                                .read(item.read())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        return HttpUtils.buildResponse(request, response);
    }



}
