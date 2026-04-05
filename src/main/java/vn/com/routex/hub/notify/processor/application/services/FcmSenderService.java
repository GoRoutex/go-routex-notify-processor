package vn.com.routex.hub.notify.processor.application.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface FcmSenderService {

    CompletableFuture<Void> sendMultiCast(
            List<String> tokens,
            String title,
            String body,
            String deeplink,
            String tripId
    );
}
