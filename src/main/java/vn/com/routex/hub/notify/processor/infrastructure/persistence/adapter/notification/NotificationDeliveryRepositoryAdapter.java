package vn.com.routex.hub.notify.processor.infrastructure.persistence.adapter.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.com.routex.hub.notify.processor.domain.notification.model.NotificationDelivery;
import vn.com.routex.hub.notify.processor.domain.notification.port.NotificationDeliveryRepositoryPort;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.notification.repository.NotificationDeliveryEntityRepository;

import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class NotificationDeliveryRepositoryAdapter implements NotificationDeliveryRepositoryPort {

    private final NotificationDeliveryPersistenceMapper notificationDeliveryPersistenceMapper;
    private final NotificationDeliveryEntityRepository notificationDeliveryEntityRepository;

    @Override
    public void saveAll(List<NotificationDelivery> deliveries) {
        notificationDeliveryEntityRepository.saveAll(
                deliveries.stream()
                        .map(notificationDeliveryPersistenceMapper::toEntity)
                        .collect(Collectors.toList()));
    }
}
