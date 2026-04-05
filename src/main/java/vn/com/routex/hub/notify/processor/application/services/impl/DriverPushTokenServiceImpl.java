package vn.com.routex.hub.notify.processor.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.com.routex.hub.notify.processor.application.services.DriverPushTokenService;
import vn.com.routex.hub.notify.processor.domain.device.port.DeviceTokenRepositoryPort;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DriverPushTokenServiceImpl implements DriverPushTokenService {

    private final DeviceTokenRepositoryPort deviceTokenRepositoryPort;

    @Override
    public List<String> resolveTokens(String driverId) {
        return deviceTokenRepositoryPort.findActiveTokensByDriverId(driverId);
    }
}
