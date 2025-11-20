package com.user.account.app.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Global Filter that applies to all routes
 * This filter adds custom headers and logs request information
 */
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(CustomGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String requestPath = exchange.getRequest().getPath().value();
        String requestMethod = exchange.getRequest().getMethod().toString();

        logger.info("Global Filter - Incoming request: {} {}", requestMethod, requestPath);

        // Add custom request header
        exchange.getRequest()
                .mutate()
                .header("X-Request-Timestamp", Instant.now().toString());

        // Register a callback to add response headers BEFORE the response is committed
        exchange.getResponse().beforeCommit(() -> {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Add custom response headers (this is safe because response hasn't been committed yet)
            exchange.getResponse().getHeaders().add("X-Response-Timestamp", Instant.now().toString());
            exchange.getResponse().getHeaders().add("X-Response-Time-Ms", String.valueOf(duration));

            return Mono.empty();
        });

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Log the response
            logger.info("Global Filter - Response for {} {} completed in {}ms with status: {}",
                    requestMethod,
                    requestPath,
                    duration,
                    exchange.getResponse().getStatusCode());
        }));
    }

    @Override
    public int getOrder() {
        return -1; // Higher precedence
    }
}
