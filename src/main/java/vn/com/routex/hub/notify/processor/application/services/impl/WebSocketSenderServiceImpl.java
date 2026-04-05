package vn.com.routex.hub.notify.processor.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vn.com.routex.hub.notify.processor.application.services.WebSocketSenderService;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class WebSocketSenderServiceImpl implements WebSocketSenderService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public CompletableFuture<Void> send(String driverId, String title, String body, String deeplink) {

        String destination = "/topic/drivers/" + driverId + "/notifications";

        return CompletableFuture.runAsync(() -> {
            Object payload = Map.of(
                    "title", title,
                    "body", body,
                    "deeplink", deeplink
            );
            simpMessagingTemplate.convertAndSend(destination, payload);
        });
    }
}
