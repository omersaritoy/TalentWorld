package com.TalentWorld.backend.service.impl;


import com.TalentWorld.backend.excepiton.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RateLimitingService {
    private final RedisTemplate<String,String> redisTemplate;
    private static final int MAX_ATTEMPTS=5;
    private static final long WINDOW_SECONDS=60;

    public void checkLimit(String identifier,String action){
        String key=String.format("rateLimit:%s:%s",action,identifier);
        long now=System.currentTimeMillis();
        long windowStart=now-(WINDOW_SECONDS*1000);

        redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);

        Long count=redisTemplate.opsForZSet().zCard(key);

        if (count != null && count >= MAX_ATTEMPTS) {
            log.warn("Rate limit exceeded: identifier={}, action={}", identifier, action);
            throw new BusinessException(
                    "Too many attempts, please try again later",
                    "RATE_LIMIT_EXCEEDED",
                    HttpStatus.TOO_MANY_REQUESTS
            );
        }
        redisTemplate.opsForZSet().add(key, String.valueOf(now), now);
        redisTemplate.expire(key, WINDOW_SECONDS, TimeUnit.SECONDS);
    }


}
