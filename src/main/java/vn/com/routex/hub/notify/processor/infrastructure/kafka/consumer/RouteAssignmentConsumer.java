package vn.com.routex.hub.notify.processor.infrastructure.kafka.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import vn.com.routex.hub.notify.processor.application.routeassignment.HandleRouteAssignedEventCommand;
import vn.com.routex.hub.notify.processor.application.routeassignment.HandleRouteAssignedEventUseCase;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.RouteAssignedEvent;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.model.KafkaEventMessage;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.utils.JsonUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class RouteAssignmentConsumer {

    private final HandleRouteAssignedEventUseCase handleRouteAssignedEventUseCase;

    @Value("${spring.kafka.events.route-assigned}")
    private String routeAssignedEventName;

    @KafkaListener(
            topics = "${spring.kafka.topics.routes}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${spring.kafka.group-id.routes}"
    )
    public void consume(ConsumerRecord<String, String> record, Acknowledgment ack) {
        KafkaEventMessage<RouteAssignedEvent> message = null;
        RouteAssignedEvent payload = null;

        try {
            message = JsonUtils.parseToKafkaObject(record.value(), new TypeReference<>() {});
            payload = message.data();
        } catch (Exception ignored) {
            // Some producers may publish the payload directly (without an envelope).
        }

        // Fallback: message body is the payload itself.
        if (payload == null) {
            try {
                payload = JsonUtils.parseToKafkaObject(record.value(), new TypeReference<>() {});
            } catch (Exception ex) {
                log.error(
                        "Failed to parse route-assigned event message. topic={}, partition={}, offset={}, key={}",
                        record.topic(),
                        record.partition(),
                        record.offset(),
                        record.key(),
                        ex
                );
                throw ex;
            }
        }

        // This consumer is scoped to RouteAssignment-related events; ignore others on the same topic.
        if (message != null && message.eventName() != null && !message.eventName().equals(routeAssignedEventName)) {
            log.debug(
                    "Ignored event. topic={}, partition={}, offset={}, key={}, eventName={}",
                    record.topic(),
                    record.partition(),
                    record.offset(),
                    record.key(),
                    message.eventName()
            );
            ack.acknowledge();
            return;
        }

        String eventId = message != null && message.eventId() != null
                ? message.eventId()
                : record.topic() + ":" + record.partition() + ":" + record.offset();

        String aggregateId = message != null && message.aggregateId() != null
                ? message.aggregateId()
                : (payload.routeId() != null ? payload.routeId() : record.key());

        try {
            handleRouteAssignedEventUseCase.handle(
                    new HandleRouteAssignedEventCommand(
                            message != null ? message.requestId() : null,
                            message != null ? message.requestDateTime() : null,
                            message != null ? message.channel() : null,
                            eventId,
                            aggregateId,
                            message != null ? message.occurredAt() : null,
                            payload
                    )
            );

            ack.acknowledge();
        } catch (Exception ex) {
            // Let the configured DefaultErrorHandler handle retries + DLQ publishing.
            log.error(
                    "Failed to handle route-assigned event. topic={}, partition={}, offset={}, key={}, eventId={}",
                    record.topic(),
                    record.partition(),
                    record.offset(),
                    record.key(),
                    eventId,
                    ex
            );
            throw ex;
        }
    }
}
