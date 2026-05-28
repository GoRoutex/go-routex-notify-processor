package vn.com.routex.hub.notify.processor.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import vn.com.go.routex.identity.security.log.SystemLog;
import vn.com.routex.hub.notify.processor.application.command.notification.GetNotificationHistoryQuery;
import vn.com.routex.hub.notify.processor.application.command.notification.GetNotificationHistoryResult;
import vn.com.routex.hub.notify.processor.application.command.notification.GetNotificationHistoryResult.NotificationHistoryItem;
import vn.com.routex.hub.notify.processor.application.command.notification.NotifyAllReadMarkedCommand;
import vn.com.routex.hub.notify.processor.application.command.notification.NotifyAllReadMarkedResult;
import vn.com.routex.hub.notify.processor.application.command.notification.NotifyReadMarkedCommand;
import vn.com.routex.hub.notify.processor.application.command.notification.NotifyReadMarkedResult;
import vn.com.routex.hub.notify.processor.application.command.sse.SSEStreamInformation;
import vn.com.routex.hub.notify.processor.application.services.NotificationSSEService;
import vn.com.routex.hub.notify.processor.domain.notification.model.Notification;
import vn.com.routex.hub.notify.processor.domain.notification.port.NotificationRepositoryPort;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.exception.BusinessException;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.utils.ExceptionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static vn.com.routex.hub.notify.processor.infrastructure.persistence.constant.ErrorConstant.NOTIFICATION_NOT_FOUND;
import static vn.com.routex.hub.notify.processor.infrastructure.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static vn.com.routex.hub.notify.processor.interfaces.controller.SseNotificationController.emitters;


@Service
@RequiredArgsConstructor
public class NotificationSSEServiceImpl implements NotificationSSEService {

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());
    private final NotificationRepositoryPort notificationRepositoryPort;


    @Override
    public SseEmitter streamNotification(SSEStreamInformation information) {
        sLog.info("[SSE] Subscribing user {} under merchant {}", information.email(), information.merchantId());

        // Timeout 30 mins
        SseEmitter emitter = new SseEmitter(1800_000L);

        emitters.computeIfAbsent(information.merchantId(), k -> new ConcurrentHashMap<>()).put(information.email(), emitter);

        emitter.onCompletion(() -> removeEmitter(information.merchantId(), information.email()));
        emitter.onTimeout(() -> removeEmitter(information.merchantId(), information.email()));
        emitter.onError((ex) -> removeEmitter(information.merchantId(), information.email()));

        try {
            // Send initial connection establish event
            emitter.send(SseEmitter.event()
                    .name("CONNECTED")
                    .data(Map.of("message", "SSE Connection established for " + information.email())));
        } catch (IOException e) {
            removeEmitter(information.merchantId(), information.email());
        }

        return emitter;
    }

    @Override
    public GetNotificationHistoryResult fetchHistory(GetNotificationHistoryQuery query) {
        sLog.info("[NOTIFY-HISTORY] Fetching notifications for email={}, merchantId={}", query.email(), query.merchantId());
        Page<Notification> notifications = notificationRepositoryPort
                .findByMerchantIdAndUserEmailOrderByCreatedAtDesc(query.merchantId(), query.email(), query.pageNumber(), query.pageSize());

        return GetNotificationHistoryResult.builder()
                .totalElements(notifications.getTotalElements())
                .totalPages(notifications.getTotalPages())
                .pageNumber(notifications.getNumber())
                .pageSize(notifications.getSize())
                .items(notifications.getContent().stream()
                        .map(this::toNotificationHistoryItem)
                        .toList())
                .build();
    }

    @Override
    public NotifyReadMarkedResult markAsRead(NotifyReadMarkedCommand command) {
        sLog.info("[NOTIFY-READ] Marking notification={} as read", command.id());
        Notification notification = notificationRepositoryPort.findById(command.id())
                .orElseThrow(() -> new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, NOTIFICATION_NOT_FOUND)));

        notification.setRead(true);
        notificationRepositoryPort.save(notification);
        return NotifyReadMarkedResult.builder()
                .id(notification.getId())
                .read(notification.isRead())
                .build();
    }

    @Override
    public NotifyAllReadMarkedResult markAllAsRead(NotifyAllReadMarkedCommand command) {
        sLog.info("[NOTIFY-READ-ALL] Marking all notifications for email={}, merchantId={} as read", command.email(), command.merchantId());
        List<Notification> unreadList = notificationRepositoryPort.findUnreadNotification(command.email(), command.merchantId());

        unreadList.forEach(notification -> {
            notification.setRead(true);
        });

        notificationRepositoryPort.saveAll(unreadList);

        return NotifyAllReadMarkedResult.builder()
                .item(unreadList.stream()
                        .map(not -> NotifyReadMarkedResult.builder()
                                .id(not.getId())
                                .read(not.isRead())
                                .build())
                        .toList())
                .build();
    }

    private void removeEmitter(String merchantId, String email) {
        Map<String, SseEmitter> merchantEmitters = emitters.get(merchantId);
        if (merchantEmitters != null) {
            merchantEmitters.remove(email);
            if (merchantEmitters.isEmpty()) {
                emitters.remove(merchantId);
            }
        }
    }

    private NotificationHistoryItem toNotificationHistoryItem(Notification not) {
        return NotificationHistoryItem.builder()
                .id(not.getId())
                .routeId(not.getRouteId())
                .dedupeKey(not.getDedupeKey())
                .driverId(not.getDriverId())
                .channel(not.getChannel())
                .type(not.getType())
                .title(not.getTitle())
                .body(not.getBody())
                .deepLink(not.getDeepLink())
                .payload(not.getPayload())
                .status(not.getStatus())
                .merchantId(not.getMerchantId())
                .sentAt(not.getSentAt())
                .userEmail(not.getUserEmail())
                .read(not.isRead())
                .build();
    }
}
