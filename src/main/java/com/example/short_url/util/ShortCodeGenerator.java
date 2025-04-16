package com.example.short_url.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShortCodeGenerator {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String COUNTER_KEY = "shortUrl:counter";

    public String generateShortCode() {
        // Redis에서 원자적으로 1씩 증가된 값을 가져옴
        Long counter = redisTemplate.opsForValue().increment(COUNTER_KEY, 1);
        if (counter == null) {
            throw new IllegalStateException("Redis 카운터 값을 가져오지 못했습니다.");
        }

        // Base62로 인코딩하여 shortCode 생성
        return Base62Encoder.encode(counter);
    }
}
