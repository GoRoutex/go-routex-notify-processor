package vn.com.routex.hub.notify.processor.application.services;

import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.TicketIssuedEvent;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.model.KafkaEventMessage;

public interface TicketIssuedNotificationService {
    void process(KafkaEventMessage<TicketIssuedEvent> event);
}
