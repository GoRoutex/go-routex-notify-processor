package vn.com.routex.hub.notify.processor.infrastructure.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.com.go.routex.identity.security.log.SystemLog;
import vn.com.routex.hub.notify.processor.application.services.RecentActivityService;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.DomainEvent;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.utils.JsonUtils;

@Component
@RequiredArgsConstructor
public class RecentActivitiesConsumer {

    @Value("${spring.kafka.events.recent-activity-prefix:routex.recent-activity}")
    private String recentActivityEventPrefix;

    private final RecentActivityService recentActivityService;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @KafkaListener(
            topics = "${spring.kafka.topics.activities}",
            groupId = "${spring.kafka.group-id.activities}"
    )
    public void handle(String payload) throws JsonProcessingException {
        DomainEvent event = JsonUtils.parseToObject(payload, DomainEvent.class);

        if (event == null || event.eventType() == null) {
            return;
        }

        if (!event.eventType().startsWith(recentActivityEventPrefix)) {
            return;
        }

        sLog.info("[RECENT-ACTIVITY] Event consumed successfully: eventType={}, eventId={}",
                event.eventType(), event.eventId());

        recentActivityService.record(event);
    }
}
