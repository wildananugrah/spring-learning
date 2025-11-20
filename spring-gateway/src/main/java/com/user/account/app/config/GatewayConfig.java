package com.user.account.app.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * Custom Gateway Route Configuration
 * This demonstrates programmatic route configuration using Java code
 */
@Configuration
public class GatewayConfig {

    /**
     * Define custom routes programmatically
     * This is an alternative to YAML configuration
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Route 1: Simple routing with path predicate
                .route("github_route", r -> r
                        .path("/github/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Custom-Header", "CustomValue")
                                .addResponseHeader("X-Response-Gateway", "SpringCloudGateway"))
                        .uri("https://api.github.com"))

                // Route 2: Route with method predicate
                .route("get_only_route", r -> r
                        .path("/get-only/**")
                        .and()
                        .method("GET")
                        .filters(f -> f.stripPrefix(1))
                        .uri("https://httpbin.org"))

                // Route 3: Route with host predicate
                .route("host_route", r -> r
                        .host("*.example.com")
                        .and()
                        .path("/api/**")
                        .uri("https://httpbin.org"))

                // Route 4: Route with query parameter predicate
                .route("query_route", r -> r
                        .path("/search/**")
                        .and()
                        .query("q")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestParameter("source", "gateway"))
                        .uri("https://httpbin.org"))

                // Route 5: Route with custom filter
                .route("custom_filter_route", r -> r
                        .path("/custom/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter((exchange, chain) -> {
                                    exchange.getRequest()
                                            .mutate()
                                            .header("X-Request-Time", LocalDateTime.now().toString());
                                    return chain.filter(exchange);
                                }))
                        .uri("https://httpbin.org"))

                .build();
    }
}
