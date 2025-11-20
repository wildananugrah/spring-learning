package com.springbasic.di;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller to demonstrate Dependency Injection
 *
 * This controller exposes REST endpoints to demonstrate:
 * 1. Constructor injection (RECOMMENDED)
 * 2. Field injection (NOT RECOMMENDED)
 * 3. Setter injection (for optional dependencies)
 * 4. @Primary and @Qualifier annotations
 * 5. Interface-based dependency injection
 *
 * Endpoints:
 * - POST /api/di/constructor/notify - Constructor injection demo
 * - POST /api/di/setter/order - Setter injection demo
 * - POST /api/di/qualifier/sms - @Qualifier demo
 * - GET /api/di/compare - Compares all DI types
 * - POST /api/di/report - Bean method parameter injection
 *
 * @author Spring Basic Tutorial
 */
@RestController
@RequestMapping("/api/di")
public class DIDemoController {

    // Constructor injection - RECOMMENDED
    private final NotificationService notificationService;
    private final OrderService orderService;
    private final DIConfig.ReportService reportService;
    private final MessageService emailService;
    private final MessageService smsService;

    /**
     * Constructor with @Qualifier annotations
     *
     * IMPORTANT: When using @Qualifier with constructor injection, you CANNOT use
     * @RequiredArgsConstructor from Lombok. You must write the constructor manually
     * to apply @Qualifier to the constructor parameters.
     */
    public DIDemoController(
            NotificationService notificationService,
            OrderService orderService,
            DIConfig.ReportService reportService,
            @Qualifier("emailMessageService") MessageService emailService,
            @Qualifier("smsMessageService") MessageService smsService) {
        this.notificationService = notificationService;
        this.orderService = orderService;
        this.reportService = reportService;
        this.emailService = emailService;
        this.smsService = smsService;
    }

    /**
     * Demonstrates constructor injection with NotificationService
     * NotificationService itself uses constructor injection for its dependencies
     *
     * @param request notification request
     * @return notification result
     */
    @PostMapping("/constructor/notify")
    public Map<String, Object> demonstrateConstructorInjection(@RequestBody NotificationRequest request) {
        String result = notificationService.sendNotification(
            request.getRecipient(),
            request.getMessage()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("injectionType", "CONSTRUCTOR INJECTION");
        response.put("description", "NotificationService is injected via constructor");
        response.put("result", result);
        response.put("serviceInfo", notificationService.getServiceInfo());
        response.put("advantages", new String[]{
            "Immutable (final fields)",
            "Required dependencies cannot be null",
            "Easy to test without Spring",
            "Makes dependencies explicit",
            "Prevents circular dependencies"
        });
        response.put("recommendation", "This is the RECOMMENDED approach");

        return response;
    }

    /**
     * Demonstrates multi-channel notification
     *
     * @param request notification request
     * @return notification result
     */
    @PostMapping("/constructor/multi-channel")
    public Map<String, Object> demonstrateMultiChannelNotification(@RequestBody NotificationRequest request) {
        String result = notificationService.sendMultiChannelNotification(
            request.getRecipient(),
            request.getMessage()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("injectionType", "CONSTRUCTOR INJECTION with @Qualifier");
        response.put("description", "NotificationService uses both @Primary and @Qualifier to inject specific implementations");
        response.put("result", result);
        response.put("history", notificationService.getNotificationHistory());

        return response;
    }

    /**
     * Demonstrates setter injection with OrderService
     *
     * @param request order request
     * @return order result
     */
    @PostMapping("/setter/order")
    public Map<String, Object> demonstrateSetterInjection(@RequestBody OrderRequest request) {
        OrderService.Order order = orderService.createOrderWithSmsNotification(
            request.getCustomerEmail(),
            request.getCustomerPhone(),
            request.getProductName(),
            request.getQuantity()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("injectionType", "SETTER INJECTION");
        response.put("description", "OrderService dependencies are injected via setter methods");
        response.put("order", order);
        response.put("serviceInfo", orderService.getServiceInfo());
        response.put("useCases", new String[]{
            "Optional dependencies",
            "Dependencies that can change after construction",
            "Reconfiguration scenarios"
        });
        response.put("note", "Use only when truly needed. Constructor injection is preferred.");

        return response;
    }

    /**
     * Demonstrates @Qualifier annotation to inject specific implementation
     *
     * @param request SMS request
     * @return SMS result
     */
    @PostMapping("/qualifier/sms")
    public Map<String, Object> demonstrateQualifier(@RequestBody NotificationRequest request) {
        // Using the SMS service injected with @Qualifier
        String result = smsService.sendMessage(
            request.getRecipient(),
            request.getMessage()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("injectionType", "@Qualifier Annotation");
        response.put("description", "Uses @Qualifier to inject SmsMessageService instead of default @Primary");
        response.put("result", result);
        response.put("serviceType", smsService.getServiceType());
        response.put("explanation", "@Qualifier allows you to specify which bean to inject when multiple implementations exist");

        return response;
    }

    /**
     * Demonstrates @Primary annotation
     *
     * @param request email request
     * @return email result
     */
    @PostMapping("/primary/email")
    public Map<String, Object> demonstratePrimary(@RequestBody NotificationRequest request) {
        // Using the email service (marked as @Primary)
        String result = emailService.sendMessage(
            request.getRecipient(),
            request.getMessage()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("injectionType", "@Primary Annotation");
        response.put("description", "EmailMessageService is marked with @Primary");
        response.put("result", result);
        response.put("serviceType", emailService.getServiceType());
        response.put("explanation", "@Primary makes EmailMessageService the default when no @Qualifier is specified");

        return response;
    }

    /**
     * Compares all dependency injection types
     *
     * @return comparison of DI types
     */
    @GetMapping("/compare")
    public Map<String, Object> compareDITypes() {
        Map<String, Object> constructor = new HashMap<>();
        constructor.put("type", "Constructor Injection");
        constructor.put("annotation", "@RequiredArgsConstructor (Lombok) or explicit constructor");
        constructor.put("immutability", "Yes (final fields)");
        constructor.put("testability", "Excellent - can create instance without Spring");
        constructor.put("nullSafety", "Yes - dependencies are required");
        constructor.put("recommendation", "RECOMMENDED - Use this by default");

        Map<String, Object> field = new HashMap<>();
        field.put("type", "Field Injection");
        field.put("annotation", "@Autowired on fields");
        field.put("immutability", "No (cannot use final)");
        field.put("testability", "Poor - requires reflection or Spring context");
        field.put("nullSafety", "No - fields can be null");
        field.put("recommendation", "NOT RECOMMENDED - Avoid in new code");

        Map<String, Object> setter = new HashMap<>();
        setter.put("type", "Setter Injection");
        setter.put("annotation", "@Autowired on setter methods");
        setter.put("immutability", "No (cannot use final)");
        setter.put("testability", "Good - can call setters in tests");
        setter.put("nullSafety", "No - dependencies are optional");
        setter.put("recommendation", "Use only for optional dependencies");

        Map<String, Object> response = new HashMap<>();
        response.put("constructorInjection", constructor);
        response.put("fieldInjection", field);
        response.put("setterInjection", setter);
        response.put("bestPractice", "Always prefer constructor injection unless you have a specific reason to use setter injection");

        return response;
    }

    /**
     * Demonstrates bean method parameter injection
     *
     * @param request report request
     * @return report result
     */
    @PostMapping("/report")
    public Map<String, Object> demonstrateBeanMethodInjection(@RequestBody ReportRequest request) {
        String result = reportService.generateAndSendReport(
            request.getRecipient(),
            request.getReportType()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("injectionType", "Bean Method Parameter Injection");
        response.put("description", "ReportService is created in DIConfig with injected MessageService parameter");
        response.put("result", result);
        response.put("reportsGenerated", reportService.getReportsGenerated());
        response.put("messageServiceType", reportService.getMessageServiceType());
        response.put("explanation", "Spring automatically injects dependencies into @Bean method parameters");

        return response;
    }

    /**
     * Gets notification history
     *
     * @return notification history
     */
    @GetMapping("/history")
    public Map<String, Object> getNotificationHistory() {
        return Map.of(
            "history", notificationService.getNotificationHistory(),
            "totalNotifications", notificationService.getNotificationHistory().size()
        );
    }

    /**
     * Gets all orders
     *
     * @return all orders
     */
    @GetMapping("/orders")
    public Map<String, Object> getAllOrders() {
        return Map.of(
            "orders", orderService.getAllOrders(),
            "totalOrders", orderService.getAllOrders().size()
        );
    }

    /**
     * Request data classes
     */
    @lombok.Data
    public static class NotificationRequest {
        private String recipient;
        private String message;
    }

    @lombok.Data
    public static class OrderRequest {
        private String customerEmail;
        private String customerPhone;
        private String productName;
        private int quantity;
    }

    @lombok.Data
    public static class ReportRequest {
        private String recipient;
        private String reportType;
    }
}
