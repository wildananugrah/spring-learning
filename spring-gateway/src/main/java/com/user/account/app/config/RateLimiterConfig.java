package com.user.account.app.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * Rate Limiter Configuration for Spring Cloud Gateway
 *
 * This configuration provides different key resolvers for rate limiting:
 * 1. IP-based rate limiting (default)
 * 2. User-based rate limiting (from Authorization header)
 * 3. API Key-based rate limiting (from X-API-Key header)
 */
@Configuration
public class RateLimiterConfig {

    /**
     * Primary KeyResolver - Limits requests per IP address
     * Use this for anonymous/public endpoints
     */
    @Primary
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest()
                    .getRemoteAddress()
                    .getAddress()
                    .getHostAddress();
            return Mono.just(ip);
        };
    }

    /**
     * User-based KeyResolver - Limits requests per authenticated user
     * Extracts user ID from JWT token in Authorization header
     * Use this for authenticated endpoints
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // Try to get user from Authorization header (JWT token)
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // In production, decode JWT and extract user ID
                // For now, use the token itself as key
                return Mono.just(authHeader);
            }

            // Fallback to IP if no auth header
            return Mono.just(
                exchange.getRequest()
                    .getRemoteAddress()
                    .getAddress()
                    .getHostAddress()
            );
        };
    }

    /**
     * API Key-based KeyResolver - Limits requests per API key
     * Use this for partner/third-party integrations
     */
    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> {
            String apiKey = exchange.getRequest()
                    .getHeaders()
                    .getFirst("X-API-Key");

            if (apiKey != null) {
                return Mono.just(apiKey);
            }

            // Fallback to IP if no API key
            return Mono.just(
                exchange.getRequest()
                    .getRemoteAddress()
                    .getAddress()
                    .getHostAddress()
            );
        };
    }

    /**
     * Path-based KeyResolver - Different limits per endpoint
     * Use this to limit specific endpoints differently
     */
    @Bean
    public KeyResolver pathKeyResolver() {
        return exchange -> {
            String path = exchange.getRequest().getPath().value();
            String ip = exchange.getRequest()
                    .getRemoteAddress()
                    .getAddress()
                    .getHostAddress();

            // Combine path and IP for unique rate limit per endpoint per user
            return Mono.just(path + "_" + ip);
        };
    }
}
