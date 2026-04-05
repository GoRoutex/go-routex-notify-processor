package vn.com.routex.hub.notify.processor.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import vn.com.routex.hub.notify.processor.application.services.NotificationIdempotencyService;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class NotificationIdempotencyServiceImpl implements NotificationIdempotencyService {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean acquire(String idemKey) {
        Boolean result = stringRedisTemplate.opsForValue()
                .setIfAbsent(idemKey, "1", Duration.ofDays(7));

        return Boolean.TRUE.equals(result);
    }
}
