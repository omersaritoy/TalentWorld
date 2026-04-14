package com.TalentWorld.backend.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String,String> redisTemplate;
    private static final String BLACKLIST_PREFIX = "blacklist:";


    public void blacklist(String token, long expirationMs) {
        redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + token,
                "true",
                expirationMs,
                TimeUnit.MILLISECONDS
        );
        log.info("Token blacklisted");
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}
