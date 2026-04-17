package vn.com.routex.hub.notify.processor.application.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.com.routex.hub.notify.processor.application.command.notification.NotificationCommand;
import vn.com.routex.hub.notify.processor.application.command.notification.NotificationResult;
import vn.com.routex.hub.notify.processor.application.command.notification.TemplateRendererResult;
import vn.com.routex.hub.notify.processor.application.services.DriverPushTokenService;
import vn.com.routex.hub.notify.processor.application.services.FcmSenderService;
import vn.com.routex.hub.notify.processor.application.services.NotificationPipelineService;
import vn.com.routex.hub.notify.processor.application.services.NotificationTemplateRenderer;
import vn.com.routex.hub.notify.processor.application.services.WebSocketSenderService;
import vn.com.routex.hub.notify.processor.domain.notification.NotificationStatus;
import vn.com.routex.hub.notify.processor.domain.notification.model.Notification;
import vn.com.routex.hub.notify.processor.domain.notification.model.NotificationDelivery;
import vn.com.routex.hub.notify.processor.domain.notification.port.NotificationDeliveryRepositoryPort;
import vn.com.routex.hub.notify.processor.domain.notification.port.NotificationRepositoryPort;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationPipelineServiceImpl implements NotificationPipelineService {

    private final DriverPushTokenService driverPushTokenService;
    private final NotificationTemplateRenderer notificationTemplateRenderer;
    private final NotificationRepositoryPort notificationRepositoryPort;
    private final NotificationDeliveryRepositoryPort notificationDeliveryRepositoryPort;
    private final FcmSenderService fcmSenderService;
    private final WebSocketSenderService webSocketSenderService;


    @Override
    public NotificationResult process(NotificationCommand notificationCommand) throws JsonProcessingException {
        List<String> tokens = driverPushTokenService.resolveTokens(notificationCommand.driverId());

        TemplateRendererResult result = notificationTemplateRenderer.render(
                notificationCommand.templateCode(),
                merge(notificationCommand.titleVariables(), notificationCommand.bodyVariables(), notificationCommand.metadata()),
                notificationCommand.deepLink()
        );


        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .routeId(notificationCommand.routeId())
                .driverId(notificationCommand.driverId())
                .type(notificationCommand.type())
                .title(result.title())
                .body(result.body())
                .deepLink(result.deeplink())
                .payload(notificationCommand.metadata())
                .status(NotificationStatus.CREATED)
                .createdAt(OffsetDateTime.now())
                .build();

        notificationRepositoryPort.save(notification);

       // README: This is for the first FCM Provider for notification delivery

        List<NotificationDelivery> deliveries = tokens.stream()
                .map(token -> NotificationDelivery.builder()
                        .id(UUID.randomUUID().toString())
                        .notificationId(notification.getId())
                        .provider("FCM")
                        .target(token)
                        .status("PENDING")
                        .attemptedAt(OffsetDateTime.now())
                        .build())
                .collect(Collectors.toList());


        // README: Create notification delivery for WS provider
        deliveries.add(NotificationDelivery.builder()
                .id(UUID.randomUUID().toString())
                .notificationId(notification.getId())
                .provider("WS")
                .target(notificationCommand.driverId())
                .status("PENDING")
                .attemptedAt(OffsetDateTime.now())
                .build());


        notificationDeliveryRepositoryPort.saveAll(deliveries);

        // Fire-and-forget sending; status updates will be persisted when async tasks complete.
        sendAsync(notification, deliveries, tokens);

        return new NotificationResult();
    }


    private Map<String, Object> merge(Map<String, Object> a, Map<String, Object> b, Map<String, Object> c) {
        Map<String, Object> result = new HashMap<>();

        if(a != null) result.putAll(a);
        if(b != null) result.putAll(b);
        if(c != null) result.putAll(c);

        return result;
    }

    private void sendAsync(Notification notification,
                           List<NotificationDelivery> deliveries,
                           List<String> tokens) {
        CompletableFuture<Void> fcmFuture = fcmSenderService.sendMultiCast(
                tokens,
                notification.getTitle(),
                notification.getBody(),
                notification.getDeepLink(),
                notification.getRouteId()
        );

        CompletableFuture<Void> wsFuture = webSocketSenderService.send(
                notification.getDriverId(),
                notification.getTitle(),
                notification.getBody(),
                notification.getDeepLink()
        );

        CompletableFuture.allOf(fcmFuture, wsFuture)
                .whenComplete((r, ex) -> {
                    OffsetDateTime now = OffsetDateTime.now();

                    if(ex == null) {
                        notification.setStatus(NotificationStatus.SENT);
                        deliveries.forEach(d -> {
                            d.setStatus("SUCCESS");
                            d.setAttemptedAt(now);
                        });
                    } else {
                        notification.setStatus(NotificationStatus.PARTIAL_FAILED);
                        deliveries.forEach(d -> {
                            if("PENDING".equals(d.getStatus())) {
                                d.setStatus("FAILED");
                                d.setErrorMessage(ex.getMessage());
                                d.setAttemptedAt(now);
                            }
                        });
                    }

                    notificationRepositoryPort.save(notification);
                    notificationDeliveryRepositoryPort.saveAll(deliveries);
                });
    }
}
