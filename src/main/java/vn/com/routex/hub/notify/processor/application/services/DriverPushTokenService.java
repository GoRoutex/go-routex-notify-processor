package vn.com.routex.hub.notify.processor.application.services;

import java.util.List;

public interface DriverPushTokenService {
    List<String> resolveTokens(String driverId);
}
