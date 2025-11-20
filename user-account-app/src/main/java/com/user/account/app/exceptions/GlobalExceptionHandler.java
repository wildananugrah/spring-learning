package com.user.account.app.exceptions;

import com.user.account.app.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        logException(ex, request, HttpStatus.NOT_FOUND);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request) {
        logException(ex, request, HttpStatus.CONFLICT);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientBalance(InsufficientBalanceException ex, HttpServletRequest request) {
        logException(ex, request, HttpStatus.BAD_REQUEST);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(InvalidCredentialsException ex, HttpServletRequest request) {
        logException(ex, request, HttpStatus.UNAUTHORIZED);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        logException(ex, request, HttpStatus.BAD_REQUEST);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Validation failed")
                        .data(errors)
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex, HttpServletRequest request) {
        logException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                // .body(ApiResponse.error("Please contact your IT administrator."));
                .body(ApiResponse.error("An error occurred: " + ex.getMessage()));
    }

    /**
     * Centralized logging method for all exceptions
     * Logs: timestamp, userId, method, URI, status code, exception details
     */
    private void logException(Exception ex, HttpServletRequest request, HttpStatus status) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String userId = getUserId();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUri = queryString != null ? uri + "?" + queryString : uri;

        // Add to MDC for structured JSON logging
        MDC.put("userId", userId);
        MDC.put("httpMethod", method);
        MDC.put("httpUri", fullUri);
        MDC.put("httpStatusCode", String.valueOf(status.value()));
        MDC.put("exceptionType", ex.getClass().getSimpleName());

        log.error("""

                ==================== EXCEPTION OCCURRED ====================
                Timestamp: {}
                User ID: {}
                Method: {}
                URI: {}
                Status Code: {} ({})
                Exception Type: {}
                Error Message: {}
                Stack Trace:
                {}
                ============================================================
                """,
                timestamp,
                userId,
                method,
                fullUri,
                status.value(),
                status.getReasonPhrase(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                getStackTraceAsString(ex));

        // Clean up MDC
        MDC.clear();
    }

    /**
     * Extract user ID from security context
     */
    private String getUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {
                return authentication.getName();
            }
        } catch (Exception e) {
            log.debug("Unable to extract user ID: {}", e.getMessage());
        }
        return "anonymous";
    }

    /**
     * Convert stack trace to string (limited to first 10 lines)
     */
    private String getStackTraceAsString(Exception ex) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTrace = ex.getStackTrace();
        int limit = Math.min(10, stackTrace.length);

        for (int i = 0; i < limit; i++) {
            sb.append("    at ").append(stackTrace[i].toString()).append("\n");
        }

        if (stackTrace.length > limit) {
            sb.append("    ... ").append(stackTrace.length - limit).append(" more lines");
        }

        return sb.toString();
    }
}
