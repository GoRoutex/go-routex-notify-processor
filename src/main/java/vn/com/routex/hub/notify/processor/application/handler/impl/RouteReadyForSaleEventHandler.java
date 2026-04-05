package vn.com.routex.hub.notify.processor.application.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.com.routex.hub.notify.processor.application.handler.OutBoxEventHandler;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.DomainEvent;

@Component
@RequiredArgsConstructor
public class RouteReadyForSaleEventHandler implements OutBoxEventHandler {


    @Value("${spring.kafka.events.route-ready-for-sale}")
    private String routeReadyForSaleEvent;

    @Override
    public String getEventType() {
        return routeReadyForSaleEvent;
    }

    @Override
    public void eventHandler(DomainEvent event) {

    }
}
