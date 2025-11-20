package com.springbasic.config;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller to demonstrate Configuration
 *
 * This controller exposes REST endpoints to demonstrate:
 * 1. Reading from application.properties using @Value
 * 2. Configuration classes with @Configuration
 * 3. Using Environment to access properties
 * 4. Configuration beans and their usage
 *
 * Endpoints:
 * - GET /api/config/properties - Shows all application properties
 * - GET /api/config/database - Shows database configuration
 * - GET /api/config/features - Shows feature flags
 * - GET /api/config/environment - Shows environment-based configuration
 * - POST /api/config/database/connect - Tests database connection
 *
 * @author Spring Basic Tutorial
 */
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigDemoController {

    private final AppProperties appProperties;
    private final DatabaseConfig.DatabaseConnectionManager connectionManager;
    private final Environment environment;

    /**
     * Shows all application properties from AppProperties
     *
     * @return all application properties
     */
    @GetMapping("/properties")
    public Map<String, Object> getAllProperties() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "Configuration from application.properties via @Value");
        response.put("fullConfig", appProperties.getAllConfiguration());

        Map<String, Object> details = new HashMap<>();
        details.put("appName", appProperties.getAppName());
        details.put("appVersion", appProperties.getAppVersion());
        details.put("appDescription", appProperties.getAppDescription());
        details.put("serverPort", appProperties.getServerPort());
        details.put("contextPath", appProperties.getContextPath());
        details.put("maxUploadSize", appProperties.getMaxUploadSize());
        details.put("sessionTimeout", appProperties.getSessionTimeout());
        details.put("allowedOrigins", appProperties.getAllowedOrigins());

        response.put("details", details);
        response.put("note", "These values come from application.properties or use defaults if not configured");

        return response;
    }

    /**
     * Shows database configuration
     *
     * @return database configuration
     */
    @GetMapping("/database")
    public Map<String, Object> getDatabaseConfig() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "Database configuration from @Configuration class");
        response.put("config", appProperties.getDatabaseConfig());
        response.put("connectionManagerStatus", connectionManager.getStatus());

        Map<String, Object> details = new HashMap<>();
        details.put("url", appProperties.getDatabaseUrl());
        details.put("username", appProperties.getDatabaseUsername());
        details.put("driverClassName", appProperties.getDatabaseDriverClassName());
        details.put("poolConfig", connectionManager.getPoolConfig().toString());
        details.put("initialized", connectionManager.isInitialized());
        details.put("activeConnections", connectionManager.getActiveConnections());

        response.put("details", details);

        return response;
    }

    /**
     * Shows feature flags configuration
     *
     * @return feature flags
     */
    @GetMapping("/features")
    public Map<String, Object> getFeatureFlags() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "Feature flags from configuration");
        response.put("summary", appProperties.getFeatureFlags());

        Map<String, Boolean> features = new HashMap<>();
        features.put("emailEnabled", appProperties.isEmailEnabled());
        features.put("smsEnabled", appProperties.isSmsEnabled());
        features.put("analyticsEnabled", appProperties.isAnalyticsEnabled());

        response.put("features", features);
        response.put("note", "Feature flags allow enabling/disabling features without code changes");

        return response;
    }

    /**
     * Demonstrates using Spring Environment to access properties
     * Environment provides programmatic access to property sources
     *
     * @return environment properties
     */
    @GetMapping("/environment")
    public Map<String, Object> getEnvironmentProperties() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "Properties accessed via Spring Environment");

        // Access properties using Environment
        Map<String, Object> envProps = new HashMap<>();
        envProps.put("appName", environment.getProperty("app.name", "Default App"));
        envProps.put("appVersion", environment.getProperty("app.version", "1.0.0"));
        envProps.put("serverPort", environment.getProperty("server.port", Integer.class, 8080));
        envProps.put("emailEnabled", environment.getProperty("app.features.email-enabled", Boolean.class, true));

        response.put("properties", envProps);

        // Active profiles
        String[] activeProfiles = environment.getActiveProfiles();
        String[] defaultProfiles = environment.getDefaultProfiles();

        response.put("activeProfiles", activeProfiles.length > 0 ? activeProfiles : "none");
        response.put("defaultProfiles", defaultProfiles);

        response.put("explanation", "Environment provides programmatic access to all property sources");

        return response;
    }

    /**
     * Tests database connection
     *
     * @return connection result
     */
    @PostMapping("/database/connect")
    public Map<String, Object> testDatabaseConnection() {
        String connectResult = connectionManager.connect();

        Map<String, Object> response = new HashMap<>();
        response.put("operation", "Database Connection Test");
        response.put("result", connectResult);
        response.put("status", connectionManager.getStatus());
        response.put("activeConnections", connectionManager.getActiveConnections());

        return response;
    }

    /**
     * Disconnects from database
     *
     * @return disconnection result
     */
    @PostMapping("/database/disconnect")
    public Map<String, Object> disconnectDatabase() {
        String disconnectResult = connectionManager.disconnect();

        Map<String, Object> response = new HashMap<>();
        response.put("operation", "Database Disconnection");
        response.put("result", disconnectResult);
        response.put("status", connectionManager.getStatus());
        response.put("activeConnections", connectionManager.getActiveConnections());

        return response;
    }

    /**
     * Updates a configuration value at runtime (demonstration only)
     * Note: In production, property changes typically require application restart
     *
     * @param request configuration update request
     * @return update result
     */
    @PostMapping("/update")
    public Map<String, Object> updateConfiguration(@RequestBody Map<String, String> request) {
        String key = request.get("key");
        String value = request.get("value");

        Map<String, Object> response = new HashMap<>();
        response.put("warning", "Configuration is typically read-only at runtime");
        response.put("requestedKey", key);
        response.put("requestedValue", value);
        response.put("note", "To change configuration:\n" +
            "1. Update application.properties file\n" +
            "2. Restart the application\n" +
            "OR use Spring Cloud Config for dynamic updates");

        return response;
    }

    /**
     * Compares different configuration approaches
     *
     * @return configuration approaches comparison
     */
    @GetMapping("/approaches")
    public Map<String, Object> compareConfigurationApproaches() {
        Map<String, Object> valueAnnotation = new HashMap<>();
        valueAnnotation.put("approach", "@Value annotation");
        valueAnnotation.put("pros", new String[]{
            "Simple for individual properties",
            "Supports SpEL expressions",
            "Default values with colon syntax"
        });
        valueAnnotation.put("cons", new String[]{
            "Scattered across classes",
            "Hard to validate",
            "Not type-safe for complex configs"
        });
        valueAnnotation.put("useCase", "Simple properties, feature flags");

        Map<String, Object> configurationProperties = new HashMap<>();
        configurationProperties.put("approach", "@ConfigurationProperties");
        configurationProperties.put("pros", new String[]{
            "Type-safe",
            "Validation support",
            "Grouped related properties",
            "Better IDE support"
        });
        configurationProperties.put("cons", new String[]{
            "More verbose",
            "Requires additional class"
        });
        configurationProperties.put("useCase", "Complex configuration, grouped properties");

        Map<String, Object> environmentBean = new HashMap<>();
        environmentBean.put("approach", "Environment bean");
        environmentBean.put("pros", new String[]{
            "Programmatic access",
            "Access to all property sources",
            "Profile information",
            "Type conversion support"
        });
        environmentBean.put("cons", new String[]{
            "More verbose",
            "No default value in property key"
        });
        environmentBean.put("useCase", "Dynamic property access, conditional logic");

        Map<String, Object> response = new HashMap<>();
        response.put("valueAnnotation", valueAnnotation);
        response.put("configurationProperties", configurationProperties);
        response.put("environment", environmentBean);
        response.put("recommendation", "Use @ConfigurationProperties for complex configs, " +
            "@Value for simple properties, Environment for programmatic access");

        return response;
    }
}
