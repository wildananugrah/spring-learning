package com.springbasic.annotations;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller to demonstrate Spring Annotations
 *
 * This controller exposes REST endpoints to demonstrate:
 * 1. @Component - Generic component stereotype
 * 2. @Service - Business logic layer
 * 3. @Repository - Data access layer
 * 4. @Controller - Web MVC controller
 * 5. @RestController - REST API controller
 * 6. @Configuration - Bean configuration
 * 7. @Value - Property injection
 * 8. @Autowired - Dependency injection
 * 9. @Qualifier - Bean selection
 *
 * @author Spring Basic Tutorial
 */
@RestController
@RequestMapping("/api/annotations")
@RequiredArgsConstructor
public class AnnotationDemoController {

    private final ComponentExample componentExample;
    private final ServiceExample serviceExample;
    private final RepositoryExample repositoryExample;
    private final ConfigurationExample.CacheManager cacheManager;
    private final ConfigurationExample.SecurityManager securityManager;
    private final ConfigurationExample.MonitoringService monitoringService;
    private final ValueExample valueExample;
    private final AutowiredExample autowiredExample;
    private final QualifierExample qualifierExample;

    /**
     * Demonstrates @Component annotation
     */
    @GetMapping("/component")
    public Map<String, Object> demonstrateComponent() {
        String result = componentExample.performOperation("Demo data");

        Map<String, Object> response = new HashMap<>();
        response.put("annotation", "@Component");
        response.put("description", "Generic stereotype for Spring-managed components");
        response.put("result", result);
        response.put("info", componentExample.getComponentInfo());
        response.put("useCase", "General-purpose components, utilities, helpers");

        return response;
    }

    /**
     * Demonstrates @Service annotation
     */
    @PostMapping("/service")
    public Map<String, Object> demonstrateService(@RequestBody ServiceRequest request) {
        String transactionResult = serviceExample.performTransaction(
            request.getAmount(),
            request.getDescription()
        );

        double total = serviceExample.calculateTotalWithTax(
            request.getAmount(),
            request.getTaxRate()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("annotation", "@Service");
        response.put("description", "Business logic layer stereotype");
        response.put("transactionResult", transactionResult);
        response.put("totalWithTax", total);
        response.put("info", serviceExample.getServiceInfo());
        response.put("useCase", "Business logic, service layer, transaction coordination");

        return response;
    }

    /**
     * Demonstrates @Repository annotation
     */
    @GetMapping("/repository")
    public Map<String, Object> demonstrateRepository() {
        Map<String, Object> response = new HashMap<>();
        response.put("annotation", "@Repository");
        response.put("description", "Data access layer stereotype with exception translation");
        response.put("products", repositoryExample.findAll());
        response.put("totalProducts", repositoryExample.count());
        response.put("info", repositoryExample.getRepositoryInfo());
        response.put("useCase", "Data access objects (DAO), database operations");
        response.put("benefit", "Automatic exception translation to DataAccessException");

        return response;
    }

    /**
     * Creates a new product in repository
     */
    @PostMapping("/repository/product")
    public Map<String, Object> createProduct(@RequestBody ProductRequest request) {
        RepositoryExample.Product product = new RepositoryExample.Product(
            null,
            request.getName(),
            request.getPrice()
        );

        RepositoryExample.Product saved = repositoryExample.save(product);

        Map<String, Object> response = new HashMap<>();
        response.put("annotation", "@Repository");
        response.put("operation", "CREATE");
        response.put("product", saved);
        response.put("totalProducts", repositoryExample.count());

        return response;
    }

    /**
     * Demonstrates @Configuration annotation
     */
    @GetMapping("/configuration")
    public Map<String, Object> demonstrateConfiguration() {
        cacheManager.recordHit();
        String monitoringResult = monitoringService.monitor("Configuration test event");

        Map<String, Object> response = new HashMap<>();
        response.put("annotation", "@Configuration");
        response.put("description", "Contains bean definitions with @Bean methods");
        response.put("cacheInfo", cacheManager.getCacheInfo());
        response.put("securityInfo", securityManager.getSecurityInfo());
        response.put("monitoringResult", monitoringResult);
        response.put("monitoringInfo", monitoringService.getMonitoringInfo());
        response.put("useCase", "Creating beans from third-party libraries, complex initialization");

        return response;
    }

    /**
     * Tests security manager encryption
     */
    @PostMapping("/configuration/encrypt")
    public Map<String, Object> testEncryption(@RequestBody Map<String, String> request) {
        String data = request.getOrDefault("data", "Secret message");
        String encrypted = securityManager.encrypt(data);

        Map<String, Object> response = new HashMap<>();
        response.put("original", data);
        response.put("encrypted", encrypted);
        response.put("securityInfo", securityManager.getSecurityInfo());

        return response;
    }

    /**
     * Demonstrates @Value annotation
     */
    @GetMapping("/value")
    public Map<String, Object> demonstrateValue() {
        Map<String, Object> response = new HashMap<>();
        response.put("annotation", "@Value");
        response.put("description", "Injects values from properties files or expressions");
        response.put("applicationInfo", valueExample.getApplicationInfo());
        response.put("allValues", valueExample.getAllValues());

        Map<String, Object> examples = new HashMap<>();
        examples.put("appName", valueExample.getApplicationName());
        examples.put("appVersion", valueExample.getApplicationVersion());
        examples.put("maxUsers", valueExample.getMaxUsers());
        examples.put("debugMode", valueExample.isDebugMode());
        examples.put("languages", valueExample.getSupportedLanguages());
        examples.put("randomValue", valueExample.getRandomValue());
        examples.put("javaHome", valueExample.getJavaHome());

        response.put("examples", examples);
        response.put("useCase", "External configuration, application properties, feature flags");

        return response;
    }

    /**
     * Demonstrates @Autowired annotation
     */
    @GetMapping("/autowired")
    public Map<String, Object> demonstrateAutowired() {
        String dependenciesInfo = autowiredExample.useDependencies();
        String operationResult = autowiredExample.performOperations();

        Map<String, Object> response = new HashMap<>();
        response.put("annotation", "@Autowired");
        response.put("description", "Automatic dependency injection");
        response.put("injectionInfo", autowiredExample.getInjectionInfo());
        response.put("dependenciesInfo", dependenciesInfo);
        response.put("operationResult", operationResult);
        response.put("injectionTypes", new String[]{
            "Constructor injection (RECOMMENDED)",
            "Field injection (NOT RECOMMENDED)",
            "Setter injection (for optional dependencies)"
        });

        return response;
    }

    /**
     * Demonstrates @Qualifier annotation
     */
    @PostMapping("/qualifier/payment")
    public Map<String, Object> demonstrateQualifier(@RequestBody PaymentRequest request) {
        String result = qualifierExample.processPayment(
            request.getPaymentMethod(),
            request.getAmount()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("annotation", "@Qualifier");
        response.put("description", "Resolves autowiring ambiguity when multiple beans exist");
        response.put("result", result);
        response.put("processorInfo", qualifierExample.getProcessorInfo());
        response.put("useCase", "Multiple implementations of same interface");

        return response;
    }

    /**
     * Demonstrates all payment processors
     */
    @GetMapping("/qualifier/payment/all")
    public Map<String, Object> demonstrateAllProcessors() {
        String result = qualifierExample.demonstrateAllProcessors(100.0);

        Map<String, Object> response = new HashMap<>();
        response.put("annotation", "@Qualifier");
        response.put("description", "Three different PaymentProcessor beans injected using @Qualifier");
        response.put("result", result);
        response.put("processorInfo", qualifierExample.getProcessorInfo());
        response.put("explanation", "Without @Qualifier, Spring wouldn't know which bean to inject");

        return response;
    }

    /**
     * Compares all stereotype annotations
     */
    @GetMapping("/stereotypes/compare")
    public Map<String, Object> compareStereotypes() {
        Map<String, Object> component = new HashMap<>();
        component.put("annotation", "@Component");
        component.put("purpose", "Generic component");
        component.put("layer", "Any");
        component.put("specialFeatures", "None - base stereotype");

        Map<String, Object> service = new HashMap<>();
        service.put("annotation", "@Service");
        service.put("purpose", "Business logic");
        service.put("layer", "Service layer");
        service.put("specialFeatures", "May enable additional features in future");

        Map<String, Object> repository = new HashMap<>();
        repository.put("annotation", "@Repository");
        repository.put("purpose", "Data access");
        repository.put("layer", "Persistence layer");
        repository.put("specialFeatures", "Exception translation to DataAccessException");

        Map<String, Object> controller = new HashMap<>();
        controller.put("annotation", "@Controller");
        controller.put("purpose", "Web MVC controller");
        controller.put("layer", "Presentation layer");
        controller.put("specialFeatures", "Returns view names for templates");

        Map<String, Object> restController = new HashMap<>();
        restController.put("annotation", "@RestController");
        restController.put("purpose", "REST API controller");
        restController.put("layer", "Presentation layer");
        restController.put("specialFeatures", "@Controller + @ResponseBody combined");

        Map<String, Object> configuration = new HashMap<>();
        configuration.put("annotation", "@Configuration");
        configuration.put("purpose", "Bean definitions");
        configuration.put("layer", "Configuration");
        configuration.put("specialFeatures", "Contains @Bean methods, proxy-based");

        Map<String, Object> response = new HashMap<>();
        response.put("component", component);
        response.put("service", service);
        response.put("repository", repository);
        response.put("controller", controller);
        response.put("restController", restController);
        response.put("configuration", configuration);
        response.put("recommendation", "Use specific stereotypes (@Service, @Repository, @Controller) " +
            "instead of generic @Component for better code organization and potential framework optimizations");

        return response;
    }

    /**
     * Request DTOs
     */
    @lombok.Data
    public static class ServiceRequest {
        private double amount;
        private String description;
        private double taxRate = 0.1;
    }

    @lombok.Data
    public static class ProductRequest {
        private String name;
        private double price;
    }

    @lombok.Data
    public static class PaymentRequest {
        private String paymentMethod;
        private double amount;
    }
}
