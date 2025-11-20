package com.user.account.app.config;

import org.springframework.cloud.gateway.filter.ratelimit.AbstractRateLimiter;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-Memory Rate Limiter for Development
 *
 * This is a simple in-memory rate limiter that doesn't require Redis.
 * Uses token bucket algorithm.
 *
 * WARNING: This is for development/testing only!
 * In production, use Redis-based rate limiter for distributed systems.
 */
@Component
@Primary
public class InMemoryRateLimiter extends AbstractRateLimiter<InMemoryRateLimiter.Config> {

    // Store for each key: last refill time, current tokens
    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public InMemoryRateLimiter() {
        super(Config.class, "in-memory-rate-limiter", null);
    }

    @Override
    public Mono<Response> isAllowed(String routeId, String key) {
        Config config = getConfig().get(routeId);

        if (config == null) {
            // Default config if not specified
            config = new Config()
                    .setReplenishRate(10)
                    .setBurstCapacity(20);
        }

        TokenBucket bucket = buckets.computeIfAbsent(key, k -> new TokenBucket(
                config.getBurstCapacity(),
                config.getReplenishRate()
        ));

        boolean allowed = bucket.tryConsume(config.getRequestedTokens());

        Response response = new Response(allowed, getHeaders(config, bucket));

        return Mono.just(response);
    }

    private Map<String, String> getHeaders(Config config, TokenBucket bucket) {
        long remaining = bucket.getAvailableTokens();
        return Map.of(
                "X-RateLimit-Remaining", String.valueOf(remaining),
                "X-RateLimit-Burst-Capacity", String.valueOf(config.getBurstCapacity()),
                "X-RateLimit-Replenish-Rate", String.valueOf(config.getReplenishRate())
        );
    }

    /**
     * Configuration class for rate limiter
     */
    public static class Config {
        private int replenishRate = 10;  // tokens per second
        private int burstCapacity = 20;  // max tokens
        private int requestedTokens = 1; // tokens per request

        public int getReplenishRate() {
            return replenishRate;
        }

        public Config setReplenishRate(int replenishRate) {
            this.replenishRate = replenishRate;
            return this;
        }

        public int getBurstCapacity() {
            return burstCapacity;
        }

        public Config setBurstCapacity(int burstCapacity) {
            this.burstCapacity = burstCapacity;
            return this;
        }

        public int getRequestedTokens() {
            return requestedTokens;
        }

        public Config setRequestedTokens(int requestedTokens) {
            this.requestedTokens = requestedTokens;
            return this;
        }
    }

    /**
     * Token Bucket implementation
     */
    private static class TokenBucket {
        private final long capacity;
        private final double refillRate;
        private double tokens;
        private long lastRefillTimestamp;

        public TokenBucket(long capacity, double refillRate) {
            this.capacity = capacity;
            this.refillRate = refillRate;
            this.tokens = capacity;
            this.lastRefillTimestamp = System.currentTimeMillis();
        }

        public synchronized boolean tryConsume(int tokensToConsume) {
            refill();

            if (tokens >= tokensToConsume) {
                tokens -= tokensToConsume;
                return true;
            }

            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long timePassed = now - lastRefillTimestamp;

            // Calculate tokens to add based on time passed
            double tokensToAdd = (timePassed / 1000.0) * refillRate;

            if (tokensToAdd > 0) {
                tokens = Math.min(capacity, tokens + tokensToAdd);
                lastRefillTimestamp = now;
            }
        }

        public long getAvailableTokens() {
            return (long) Math.floor(tokens);
        }
    }
}
