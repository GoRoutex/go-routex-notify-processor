package vn.com.routex.hub.notify.processor.application.reminder;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record ReminderJobPayload(
        ReminderType type,
        String eventId,
        String bookingId,
        String bookingCode,
        String customerId,
        String customerName,
        String customerEmail,
        String merchantId,
        String tripId,
        String driverId,
        String vehicleId,
        String originName,
        String destinationName,
        OffsetDateTime departureTime,
        BigDecimal totalAmount,
        String currency,
        List<TicketItem> tickets
) {
    @Builder
    public record TicketItem(
            String ticketId,
            String ticketCode,
            String seatNumber
    ) {
    }
}
