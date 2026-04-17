package vn.com.routex.hub.notify.processor.infrastructure.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface OutBoxEventConsumer {
    void handle(String payload) throws JsonProcessingException;
}
