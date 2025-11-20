package com.user.account.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * Filter to log all HTTP requests and responses with comprehensive details
 * Logs: timestamp, userId, method, URI, status code, elapsed time
 * For errors (non-2xx): also logs request body, response body, and error message
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final int MAX_PAYLOAD_LENGTH = 5000; // Maximum characters to log for request/response body
    private static final List<String> EXCLUDE_PATHS = Arrays.asList("/actuator", "/swagger", "/v3/api-docs");

    private final ObjectMapper objectMapper;

    public RequestResponseLoggingFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip logging for excluded paths
        if (isExcludedPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);

        // Wrap request and response to cache their bodies
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            // Continue with the filter chain
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long elapsedTime = System.currentTimeMillis() - startTime;

            // Log the request/response
            logRequestResponse(wrappedRequest, wrappedResponse, timestamp, elapsedTime);

            // Copy the cached response body to the actual response
            wrappedResponse.copyBodyToResponse();

            // Clean up MDC
            MDC.clear();
        }
    }

    private void logRequestResponse(ContentCachingRequestWrapper request,
                                    ContentCachingResponseWrapper response,
                                    String timestamp,
                                    long elapsedTime) {

        String method = request.getMethod();
        String uri = request.getRequestURI();
        int statusCode = response.getStatus();
        String userId = getUserId();
        String queryString = request.getQueryString();
        String fullUri = queryString != null ? uri + "?" + queryString : uri;

        // Add to MDC for structured JSON logging
        MDC.put("userId", userId);
        MDC.put("httpMethod", method);
        MDC.put("httpUri", fullUri);
        MDC.put("httpStatusCode", String.valueOf(statusCode));
        MDC.put("elapsedTime", String.valueOf(elapsedTime));

        // Determine if this is an error response
        boolean isError = statusCode < 200 || statusCode >= 300;

        if (isError) {
            // Log detailed information for errors
            String requestBody = getRequestBody(request);
            String responseBody = getResponseBody(response);

            // Add request/response body to MDC for JSON logging
            MDC.put("requestBody", requestBody);
            MDC.put("responseBody", responseBody);

            log.error("""

                    ==================== ERROR REQUEST/RESPONSE ====================
                    Timestamp: {}
                    User ID: {}
                    Method: {}
                    URI: {}
                    Status Code: {}
                    Elapsed Time: {} ms
                    --- Request Body ---
                    {}
                    --- Response Body ---
                    {}
                    ================================================================
                    """,
                    timestamp, userId, method, fullUri, statusCode, elapsedTime,
                    requestBody, responseBody);
        } else {
            // Log summary for successful requests
            log.info("Timestamp: {} | User ID: {} | Method: {} | URI: {} | Status: {} | Elapsed Time: {} ms",
                    timestamp, userId, method, fullUri, statusCode, elapsedTime);
        }
    }

    private String getUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {
                return authentication.getName(); // This will be the email from JWT
            }
        } catch (Exception e) {
            log.debug("Unable to extract user ID: {}", e.getMessage());
        }
        return "anonymous";
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        try {
            byte[] content = request.getContentAsByteArray();
            if (content.length > 0) {
                String body = new String(content, request.getCharacterEncoding());

                // Mask sensitive data (password, etc.)
                body = maskSensitiveData(body);

                if (body.length() > MAX_PAYLOAD_LENGTH) {
                    return body.substring(0, MAX_PAYLOAD_LENGTH) + "... (truncated)";
                }
                return body;
            }
        } catch (UnsupportedEncodingException e) {
            log.debug("Unable to parse request body: {}", e.getMessage());
        }
        return "[empty or binary content]";
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        try {
            byte[] content = response.getContentAsByteArray();
            if (content.length > 0) {
                String body = new String(content, response.getCharacterEncoding());

                if (body.length() > MAX_PAYLOAD_LENGTH) {
                    return body.substring(0, MAX_PAYLOAD_LENGTH) + "... (truncated)";
                }
                return body;
            }
        } catch (UnsupportedEncodingException e) {
            log.debug("Unable to parse response body: {}", e.getMessage());
        }
        return "[empty or binary content]";
    }

    /**
     * Mask sensitive data in request body (passwords, tokens, etc.)
     */
    private String maskSensitiveData(String body) {
        // Mask password fields in JSON
        body = body.replaceAll("(\"password\"\\s*:\\s*\")[^\"]*\"", "$1***MASKED***\"");
        body = body.replaceAll("(\"token\"\\s*:\\s*\")[^\"]*\"", "$1***MASKED***\"");
        body = body.replaceAll("(\"secret\"\\s*:\\s*\")[^\"]*\"", "$1***MASKED***\"");
        return body;
    }

    private boolean isExcludedPath(String path) {
        return EXCLUDE_PATHS.stream().anyMatch(path::startsWith);
    }
}
