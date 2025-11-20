package com.springbasic.lifecycle;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller to demonstrate Spring Bean Lifecycle
 *
 * This controller exposes REST endpoints to demonstrate:
 * 1. @PostConstruct and @PreDestroy
 * 2. InitializingBean and DisposableBean interfaces
 * 3. Custom init and destroy methods via @Bean
 * 4. Real-world lifecycle example (Database connection pool)
 * 5. Bean lifecycle event tracking
 *
 * Endpoints:
 * - GET /api/lifecycle/events - View lifecycle events
 * - GET /api/lifecycle/info - Bean lifecycle information
 * - POST /api/lifecycle/work - Execute business logic
 * - GET /api/lifecycle/custom - Custom lifecycle bean demo
 * - GET /api/lifecycle/database - Database connection pool demo
 * - POST /api/lifecycle/database/connection - Get database connection
 * - POST /api/lifecycle/database/release - Release database connection
 * - GET /api/lifecycle/explanation - Lifecycle explanation
 *
 * @author Spring Basic Tutorial
 */
@RestController
@RequestMapping("/api/lifecycle")
@RequiredArgsConstructor
public class LifecycleDemoController {

    private final LifecycleBean lifecycleBean;
    private final CustomLifecycleBean customLifecycleBean;
    private final DatabaseConnectionBean databaseConnectionBean;

    /**
     * Get all lifecycle events that occurred
     */
    @GetMapping("/events")
    public Map<String, Object> getLifecycleEvents() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "Spring Bean Lifecycle Events");
        response.put("events", lifecycleBean.getLifecycleEvents());
        response.put("totalEvents", lifecycleBean.getLifecycleEvents().size());
        response.put("beanInfo", lifecycleBean.getBeanInfo());
        response.put("note", "These events were captured from bean creation to current state");

        return response;
    }

    /**
     * Get lifecycle information
     */
    @GetMapping("/info")
    public Map<String, Object> getLifecycleInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "Spring Bean Lifecycle Information");

        Map<String, Object> lifecycleBeanInfo = new HashMap<>();
        lifecycleBeanInfo.put("beanInfo", lifecycleBean.getBeanInfo());
        lifecycleBeanInfo.put("createdAt", lifecycleBean.getCreatedAt());
        lifecycleBeanInfo.put("initializedAt", lifecycleBean.getInitializedAt());
        lifecycleBeanInfo.put("isInitialized", lifecycleBean.isInitialized());
        lifecycleBeanInfo.put("eventsCount", lifecycleBean.getLifecycleEvents().size());

        response.put("lifecycleBean", lifecycleBeanInfo);
        response.put("customLifecycleBean", customLifecycleBean.getBeanInfo());
        response.put("databaseConnectionBean", databaseConnectionBean.getPoolInfo());

        return response;
    }

    /**
     * Execute business logic on lifecycle bean
     */
    @PostMapping("/work")
    public Map<String, Object> doWork(@RequestBody(required = false) Map<String, String> request) {
        String task = request != null ? request.getOrDefault("task", "Default task") : "Default task";
        String result = lifecycleBean.doWork(task);

        Map<String, Object> response = new HashMap<>();
        response.put("description", "Bean is fully initialized and ready to work");
        response.put("task", task);
        response.put("result", result);
        response.put("beanInfo", lifecycleBean.getBeanInfo());
        response.put("currentEvents", lifecycleBean.getLifecycleEvents().size());

        return response;
    }

    /**
     * Custom lifecycle bean demonstration
     */
    @GetMapping("/custom")
    public Map<String, Object> getCustomLifecycle() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "Custom init/destroy methods configured via @Bean");
        response.put("beanInfo", customLifecycleBean.getBeanInfo());
        response.put("events", customLifecycleBean.getLifecycleEvents());
        response.put("isReady", customLifecycleBean.isReady());
        response.put("explanation", "This bean uses custom init() and cleanup() methods " +
                "instead of @PostConstruct/@PreDestroy");

        return response;
    }

    /**
     * Process data with custom lifecycle bean
     */
    @PostMapping("/custom/process")
    public Map<String, Object> processData(@RequestBody Map<String, String> request) {
        String data = request.getOrDefault("data", "Sample data");
        String result = customLifecycleBean.processData(data);

        Map<String, Object> response = new HashMap<>();
        response.put("description", "Processing data with custom lifecycle bean");
        response.put("input", data);
        response.put("result", result);
        response.put("events", customLifecycleBean.getLifecycleEvents());

        return response;
    }

    /**
     * Database connection pool demo
     */
    @GetMapping("/database")
    public Map<String, Object> getDatabaseConnectionPool() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "Real-world example: Database Connection Pool Lifecycle");
        response.put("poolInfo", databaseConnectionBean.getPoolInfo());
        response.put("maxConnections", databaseConnectionBean.getMaxConnections());
        response.put("activeConnections", databaseConnectionBean.getActiveConnections());
        response.put("isConnected", databaseConnectionBean.isConnected());
        response.put("connectedAt", databaseConnectionBean.getConnectedAt());
        response.put("explanation", "@PostConstruct initialized the connection pool, " +
                "@PreDestroy will close it when application shuts down");

        return response;
    }

    /**
     * Get a database connection
     */
    @PostMapping("/database/connection")
    public Map<String, Object> getConnection() {
        String result = databaseConnectionBean.getConnection();

        Map<String, Object> response = new HashMap<>();
        response.put("operation", "Get Connection");
        response.put("result", result);
        response.put("activeConnections", databaseConnectionBean.getActiveConnections());
        response.put("maxConnections", databaseConnectionBean.getMaxConnections());
        response.put("poolInfo", databaseConnectionBean.getPoolInfo());

        return response;
    }

    /**
     * Release a database connection
     */
    @PostMapping("/database/release")
    public Map<String, Object> releaseConnection() {
        String result = databaseConnectionBean.releaseConnection();

        Map<String, Object> response = new HashMap<>();
        response.put("operation", "Release Connection");
        response.put("result", result);
        response.put("activeConnections", databaseConnectionBean.getActiveConnections());
        response.put("maxConnections", databaseConnectionBean.getMaxConnections());
        response.put("poolInfo", databaseConnectionBean.getPoolInfo());

        return response;
    }

    /**
     * Get lifecycle explanation
     */
    @GetMapping("/explanation")
    public Map<String, Object> getLifecycleExplanation() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "Spring Bean Lifecycle Phases");

        String[] phases = {
            "1. Constructor - Bean instance is created",
            "2. Dependency Injection - Dependencies are injected",
            "3. @PostConstruct - Initialization callback",
            "4. InitializingBean.afterPropertiesSet() - Alternative initialization",
            "5. Custom init method - Via @Bean(initMethod)",
            "6. Bean is ready for use - Application runs",
            "7. Container shutdown begins - Application shutting down",
            "8. @PreDestroy - Cleanup callback",
            "9. DisposableBean.destroy() - Alternative cleanup",
            "10. Custom destroy method - Via @Bean(destroyMethod)"
        };

        response.put("phases", phases);

        Map<String, String> recommendations = new HashMap<>();
        recommendations.put("Recommended", "Use @PostConstruct and @PreDestroy (standard annotations)");
        recommendations.put("Alternative", "Implement InitializingBean and DisposableBean (Spring-specific)");
        recommendations.put("ConfigBased", "Use @Bean(initMethod, destroyMethod) for third-party classes");

        response.put("recommendations", recommendations);

        Map<String, String> useCases = new HashMap<>();
        useCases.put("@PostConstruct", "Initialize resources, load cache, validate configuration");
        useCases.put("@PreDestroy", "Close connections, release resources, cleanup temp files");

        response.put("commonUseCases", useCases);

        return response;
    }

    /**
     * Compare lifecycle approaches
     */
    @GetMapping("/compare")
    public Map<String, Object> compareLifecycleApproaches() {
        Map<String, Object> postConstruct = new HashMap<>();
        postConstruct.put("approach", "@PostConstruct / @PreDestroy");
        postConstruct.put("standard", "Yes (JSR-250)");
        postConstruct.put("frameworkSpecific", "No");
        postConstruct.put("pros", new String[]{
            "Standard Java annotations",
            "Framework independent",
            "Clear and simple",
            "Most commonly used"
        });
        postConstruct.put("cons", new String[]{
            "Requires annotation processing"
        });
        postConstruct.put("recommendation", "RECOMMENDED - Use this by default");

        Map<String, Object> interfaces = new HashMap<>();
        interfaces.put("approach", "InitializingBean / DisposableBean");
        interfaces.put("standard", "No");
        interfaces.put("frameworkSpecific", "Yes (Spring)");
        interfaces.put("pros", new String[]{
            "Type-safe",
            "IDE refactoring support",
            "No annotation processing needed"
        });
        interfaces.put("cons", new String[]{
            "Couples code to Spring",
            "Less portable"
        });
        interfaces.put("recommendation", "Use when framework coupling is acceptable");

        Map<String, Object> customMethods = new HashMap<>();
        customMethods.put("approach", "@Bean(initMethod, destroyMethod)");
        customMethods.put("standard", "No");
        customMethods.put("frameworkSpecific", "Yes (Spring)");
        customMethods.put("pros", new String[]{
            "Works with third-party classes",
            "No need to modify class",
            "Flexible method names"
        });
        customMethods.put("cons", new String[]{
            "Configuration in separate place",
            "Method names as strings (no IDE support)"
        });
        customMethods.put("recommendation", "Use for third-party classes you can't modify");

        Map<String, Object> response = new HashMap<>();
        response.put("description", "Comparison of Bean Lifecycle Approaches");
        response.put("postConstruct", postConstruct);
        response.put("interfaces", interfaces);
        response.put("customMethods", customMethods);
        response.put("bestPractice", "Use @PostConstruct/@PreDestroy unless you have specific reasons to use other approaches");

        return response;
    }
}
