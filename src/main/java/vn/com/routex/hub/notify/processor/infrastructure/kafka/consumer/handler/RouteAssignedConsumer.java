package vn.com.routex.hub.notify.processor.infrastructure.kafka.consumer.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.com.go.routex.identity.security.log.SystemLog;
import vn.com.routex.hub.notify.processor.application.handler.impl.OutBoxEventDispatcher;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.consumer.OutBoxEventConsumer;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.DomainEvent;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.utils.JsonUtils;


@Component
@RequiredArgsConstructor
public class RouteAssignedConsumer implements OutBoxEventConsumer {

    @Value("${spring.kafka.events.route-assigned}")
    private String routeAssignedEvent;

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());
    private final OutBoxEventDispatcher outBoxEventDispatcher;

    @Override
    @KafkaListener(
            topics = "${spring.kafka.topics.routes}",
            groupId = "${spring.kafka.group-id.routes}"

    )
    public void handle(String payload) throws JsonProcessingException {
        DomainEvent event = JsonUtils.parseToObject(payload, DomainEvent.class);
        if(!routeAssignedEvent.equals(event.eventType())) {
            return;
        }

        sLog.info("[ROUTE-ASSIGNED] Event consumed successfully with eventType={}, aggregateId={}",
                event.eventType(), event.eventId());

        outBoxEventDispatcher.dispatch(event);
    }
}
