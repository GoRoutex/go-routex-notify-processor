package vn.com.routex.hub.notify.processor.domain.device.port;

import java.util.List;

public interface DeviceTokenRepositoryPort {
    List<String> findActiveTokensByDriverId(String driverId);
}
