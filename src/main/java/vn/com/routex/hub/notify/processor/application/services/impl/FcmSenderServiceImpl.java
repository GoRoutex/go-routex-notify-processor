package vn.com.routex.hub.notify.processor.application.services.impl;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.com.go.routex.identity.security.log.SystemLog;
import vn.com.routex.hub.notify.processor.application.services.FcmSenderService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
public class FcmSenderServiceImpl implements FcmSenderService {

    private static final int MAX_TOKENS_PER_MULTICAST = 500;

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());
    private final FirebaseMessaging firebaseMessaging;

    @Value("${spring.fcm.enabled:true}")
    private boolean fcmEnabled;

    @Override
    public CompletableFuture<Void> sendMultiCast(
            List<String> tokens,
            String title,
            String body,
            String deeplink,
            String tripId
    ) {
        if (tokens == null || tokens.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.runAsync(() -> {
            if (!fcmEnabled) {
                sLog.info("FCM is disabled. Skip sending to {} tokens", tokens.size());
                return;
            }

            List<String> normalizedTokens = normalizeTokens(tokens);
            if (normalizedTokens.isEmpty()) {
                return;
            }

            sLog.info("Sending FCM to {} tokens", normalizedTokens.size());
            sendInBatches(normalizedTokens, title, body, deeplink, tripId);
        });
    }

    private List<String> normalizeTokens(List<String> tokens) {
        return tokens.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    private void sendInBatches(
            List<String> tokens,
            String title,
            String body,
            String deeplink,
            String tripId
    ) {
        for (int i = 0; i < tokens.size(); i += MAX_TOKENS_PER_MULTICAST) {
            List<String> batchTokens = tokens.subList(i, Math.min(i + MAX_TOKENS_PER_MULTICAST, tokens.size()));
            sendOneBatch(batchTokens, title, body, deeplink, tripId);
        }
    }

    private void sendOneBatch(
            List<String> batchTokens,
            String title,
            String body,
            String deeplink,
            String tripId
    ) {
        MulticastMessage message = buildMessage(batchTokens, title, body, deeplink, tripId);

        try {
            BatchResponse response = firebaseMessaging.sendEachForMulticast(message);
            logBatchResult(response, batchTokens.size());

            if (response.getSuccessCount() == 0 && response.getFailureCount() > 0) {
                throw new IllegalStateException("FCM multicast failed for all tokens in batch");
            }
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException("FCM sendEachForMulticast failed", e);
        }
    }

    private MulticastMessage buildMessage(
            List<String> batchTokens,
            String title,
            String body,
            String deeplink,
            String tripId
    ) {
        MulticastMessage.Builder messageBuilder = MulticastMessage.builder()
                .addAllTokens(batchTokens);

        // "notification" payload: shows in system tray when app in background (depending on client setup).
        if (title != null || body != null) {
            messageBuilder.setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build());
        }

        // "data" payload: for app-side routing.
        if (deeplink != null && !deeplink.isBlank()) {
            messageBuilder.putData("deeplink", deeplink);
        }
        if (tripId != null && !tripId.isBlank()) {
            messageBuilder.putData("tripId", tripId);
        }

        return messageBuilder.build();
    }

    private void logBatchResult(BatchResponse response, int total) {
        int ok = response.getSuccessCount();
        int fail = response.getFailureCount();

        if (fail > 0) {
            // We only log token-level failures here; delivery status updates can be added later.
            sLog.warn("FCM multicast batch done: success={}, failure={}, total={}", ok, fail, total);
            return;
        }

        sLog.info("FCM multicast batch done: success={}, total={}", ok, total);
    }
}
