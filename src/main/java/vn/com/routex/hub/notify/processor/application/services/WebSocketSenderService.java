package vn.com.routex.hub.notify.processor.application.services;

import java.util.concurrent.CompletableFuture;

public interface WebSocketSenderService {

    CompletableFuture<Void> send(String driverId, String title, String body, String deeplink);

}
