package vn.com.routex.hub.notify.processor.infrastructure.kafka.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.com.go.routex.identity.security.log.SystemLog;
import vn.com.routex.hub.notify.processor.application.services.TicketIssuedNotificationService;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.TicketIssuedEvent;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.model.KafkaEventMessage;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.exception.BusinessException;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.utils.ExceptionUtils;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.utils.JsonUtils;

import static vn.com.routex.hub.notify.processor.infrastructure.persistence.constant.ErrorConstant.INVALID_DATA_ERROR_MESSAGE;
import static vn.com.routex.hub.notify.processor.infrastructure.persistence.constant.ErrorConstant.INVALID_EVENT_MESSAGE;
import static vn.com.routex.hub.notify.processor.infrastructure.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;

@Component
@RequiredArgsConstructor
public class TicketIssuedConsumer {

    @Value("${spring.kafka.events.ticket-issued}")
    private String ticketIssuedEvent;

    private final TicketIssuedNotificationService ticketIssuedNotificationService;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @KafkaListener(
            topics = "${spring.kafka.topics.booking}",
            groupId = "${spring.kafka.group-id.bookings}"
    )
    public void handle(String payload) {
        KafkaEventMessage<TicketIssuedEvent> event = JsonUtils.parseToKafkaObject(
                payload,
                new TypeReference<>() {
                }
        );

        if (event == null || event.data() == null) {
            throw new BusinessException(ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_DATA_ERROR_MESSAGE));
        }

        if (!ticketIssuedEvent.equals(event.eventName())) {
            return;
        }

        TicketIssuedEvent data = event.data();
        if (isBlank(event.eventId())
                || isBlank(event.eventName())
                || isBlank(event.aggregateId())
                || isBlank(data.bookingId())
                || isBlank(data.customerId())) {
            throw new BusinessException(event.requestId(), event.requestDateTime(), event.channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, String.format(INVALID_EVENT_MESSAGE, event.eventName())));
        }

        sLog.info("[TICKET-ISSUED] Processing eventId={} bookingId={} customerId={}",
                event.eventId(), data.bookingId(), data.customerId());

        ticketIssuedNotificationService.process(event);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
