package com.springbasic.annotations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Value Annotation Example
 *
 * @Value is used to inject values from property files or expressions into fields.
 * It supports:
 * - Property placeholder resolution (${property.name})
 * - SpEL (Spring Expression Language) #{expression}
 * - Default values using colon syntax ${property:defaultValue}
 * - System properties and environment variables
 *
 * Key characteristics:
 * - Injects values from application.properties/yml
 * - Supports default values
 * - Type conversion automatic
 * - Can inject into fields, constructor params, or method params
 * - Immutable when used with final fields and constructor injection
 *
 * When to use:
 * - External configuration values
 * - Application properties
 * - Feature flags
 * - Environment-specific values
 *
 * Best practice: Use @ConfigurationProperties for complex configuration
 *
 * @author Spring Basic Tutorial
 */
@Component
public class ValueExample {

    // Simple string value with default
    @Value("${app.name:Spring Boot Application}")
    private String applicationName;

    // Numeric value with default
    @Value("${app.version:1.0.0}")
    private String applicationVersion;

    // Integer value
    @Value("${app.max-users:100}")
    private int maxUsers;

    // Boolean value
    @Value("${app.debug:false}")
    private boolean debugMode;

    // Array/List value (comma-separated)
    @Value("${app.supported-languages:English,Spanish,French}")
    private String[] supportedLanguages;

    // SpEL expression - random number
    @Value("#{T(java.lang.Math).random() * 100}")
    private double randomValue;

    // SpEL expression - current timestamp
    @Value("#{T(java.lang.System).currentTimeMillis()}")
    private long creationTimestamp;

    // System property
    @Value("${java.home:unknown}")
    private String javaHome;

    // Environment variable with default
    @Value("${USER:unknown}")
    private String systemUser;

    // Complex SpEL expression
    @Value("#{${app.max-users:100} * 2}")
    private int doubleMaxUsers;

    /**
     * Constructor
     */
    public ValueExample() {
        System.out.println("ValueExample bean created with injected values");
    }

    /**
     * Gets application information
     *
     * @return application info
     */
    public String getApplicationInfo() {
        return String.format(
            "Application: %s v%s (Max Users: %d, Debug: %s)",
            applicationName,
            applicationVersion,
            maxUsers,
            debugMode
        );
    }

    /**
     * Gets all configuration values
     *
     * @return configuration details
     */
    public String getAllValues() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== @Value Injection Demo ===\n");
        sb.append(String.format("App Name: %s\n", applicationName));
        sb.append(String.format("App Version: %s\n", applicationVersion));
        sb.append(String.format("Max Users: %d\n", maxUsers));
        sb.append(String.format("Debug Mode: %s\n", debugMode));
        sb.append(String.format("Supported Languages: %s\n", String.join(", ", supportedLanguages)));
        sb.append(String.format("Random Value: %.2f\n", randomValue));
        sb.append(String.format("Creation Timestamp: %d\n", creationTimestamp));
        sb.append(String.format("Java Home: %s\n", javaHome));
        sb.append(String.format("System User: %s\n", systemUser));
        sb.append(String.format("Double Max Users: %d\n", doubleMaxUsers));
        return sb.toString();
    }

    // Getters
    public String getApplicationName() { return applicationName; }
    public String getApplicationVersion() { return applicationVersion; }
    public int getMaxUsers() { return maxUsers; }
    public boolean isDebugMode() { return debugMode; }
    public String[] getSupportedLanguages() { return supportedLanguages; }
    public double getRandomValue() { return randomValue; }
    public long getCreationTimestamp() { return creationTimestamp; }
    public String getJavaHome() { return javaHome; }
    public String getSystemUser() { return systemUser; }
    public int getDoubleMaxUsers() { return doubleMaxUsers; }
}
