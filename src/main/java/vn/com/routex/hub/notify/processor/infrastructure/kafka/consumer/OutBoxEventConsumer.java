package vn.com.routex.hub.notify.processor.infrastructure.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface OutBoxEventConsumer {
    void handle(String payload) throws JsonProcessingException;
}
