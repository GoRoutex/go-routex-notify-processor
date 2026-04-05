package vn.com.routex.hub.notify.processor.domain.outbox;

public enum OutBoxEventStatus {
    PENDING,
    PROCESSED,
    COMPLETED,
    FAILED
}
