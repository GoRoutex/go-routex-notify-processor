package vn.com.routex.hub.notify.processor.application.command.notification;


import lombok.Builder;

import java.util.Map;

@Builder
public record NotificationCommand(
    String routeId,
    String driverId,
    String driverUserId,
    String vehicleId,
    String type,
    String templateCode,
    Map<String, Object> titleVariables,
    Map<String, Object> bodyVariables,
    String deepLink,
    Map<String, Object> metadata
) {
}
