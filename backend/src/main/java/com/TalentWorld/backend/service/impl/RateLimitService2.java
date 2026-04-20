package com.TalentWorld.backend.service.impl;


import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService2 {
    private static final int REQUEST_PER_MINUTE = 10;

    //storage for buckets(ip address-->buckets)
    private final ProxyManager<String> proxyManager;

    public Bucket resolveBucket(String key) {
        Supplier<BucketConfiguration> config = this::getConfig;

        return proxyManager.builder()
                .build(key, config);
    }

    private BucketConfiguration getConfig() {
        var limit = Bandwidth.builder()
                .capacity(REQUEST_PER_MINUTE)
                .refillGreedy(REQUEST_PER_MINUTE, Duration.ofMinutes(1))
                .build();

        return BucketConfiguration.builder()
                .addLimit(limit).build();
    }

}
