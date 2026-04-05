package vn.com.routex.hub.notify.processor.application.handler;

import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.DomainEvent;

public interface OutBoxEventHandler {

    String getEventType();
    void eventHandler(DomainEvent event);
}
