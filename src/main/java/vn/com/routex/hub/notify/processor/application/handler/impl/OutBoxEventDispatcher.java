package vn.com.routex.hub.notify.processor.application.handler.impl;


import org.springframework.stereotype.Component;
import vn.com.routex.hub.notify.processor.application.handler.OutBoxEventHandler;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.DomainEvent;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OutBoxEventDispatcher {

    private final Map<String, OutBoxEventHandler> handlerMap;

    public OutBoxEventDispatcher(List<OutBoxEventHandler> handlers) {
        this.handlerMap = handlers
                .stream()
                .collect(Collectors.toMap(
                        OutBoxEventHandler::getEventType,
                        h -> h
                ));
    }

    public void dispatch(DomainEvent event) {
        OutBoxEventHandler handler = handlerMap.get(event.eventType());
        if(handler == null) {
            throw new IllegalArgumentException("No handler for eventType = " + event.eventType());
        }
        handler.eventHandler(event);
    }
}
