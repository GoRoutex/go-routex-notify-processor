package vn.com.routex.hub.notify.processor.application.services;

public interface NotificationIdempotencyService {
    boolean acquire(String idemKey);
}
