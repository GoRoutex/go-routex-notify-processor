package vn.com.routex.hub.notify.processor.infrastructure.firebase.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.com.routex.hub.notify.processor.domain.device.port.DeviceTokenRepositoryPort;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.device.repository.DeviceTokenEntityRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DeviceTokenRepositoryAdapter implements DeviceTokenRepositoryPort {

    private final DeviceTokenEntityRepository deviceTokenEntityRepository;

    @Override
    public List<String> findActiveTokensByDriverId(String driverId) {
        return deviceTokenEntityRepository.findActiveFcmTokensByDriverId(driverId);
    }
}
