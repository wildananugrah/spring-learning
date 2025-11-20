package com.springbasic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Application Properties Configuration
 *
 * This class demonstrates reading configuration values from application.properties
 * using @Value annotation.
 *
 * application.properties example:
 * <pre>
 * # Application Configuration
 * app.name=Spring Boot Tutorial
 * app.version=1.0.0
 * app.description=Comprehensive Spring Boot basics tutorial
 *
 * # Server Configuration
 * server.port=8080
 * server.servlet.context-path=/
 *
 * # Database Configuration
 * app.database.url=jdbc:mysql://localhost:3306/springdb
 * app.database.username=root
 * app.database.password=password
 * app.database.driver-class-name=com.mysql.cj.jdbc.Driver
 *
 * # Feature Flags
 * app.features.email-enabled=true
 * app.features.sms-enabled=false
 * app.features.analytics-enabled=true
 *
 * # Business Configuration
 * app.business.max-upload-size=10485760
 * app.business.session-timeout=3600
 * app.business.allowed-origins=http://localhost:3000,http://localhost:4200
 * </pre>
 *
 * @author Spring Basic Tutorial
 */
@Component
public class AppProperties {

    // Application Information
    @Value("${app.name:Spring Boot Application}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.description:Default description}")
    private String appDescription;

    // Server Configuration
    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    // Database Configuration
    @Value("${app.database.url:jdbc:mysql://localhost:3306/defaultdb}")
    private String databaseUrl;

    @Value("${app.database.username:root}")
    private String databaseUsername;

    @Value("${app.database.password:}")
    private String databasePassword;

    @Value("${app.database.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String databaseDriverClassName;

    // Feature Flags
    @Value("${app.features.email-enabled:true}")
    private boolean emailEnabled;

    @Value("${app.features.sms-enabled:false}")
    private boolean smsEnabled;

    @Value("${app.features.analytics-enabled:true}")
    private boolean analyticsEnabled;

    // Business Configuration
    @Value("${app.business.max-upload-size:10485760}")  // 10MB default
    private long maxUploadSize;

    @Value("${app.business.session-timeout:3600}")  // 1 hour default
    private int sessionTimeout;

    @Value("${app.business.allowed-origins:http://localhost:3000}")
    private String[] allowedOrigins;

    /**
     * Constructor
     */
    public AppProperties() {
        System.out.println("AppProperties bean created - Loading configuration from application.properties");
    }

    /**
     * Gets application information
     *
     * @return application info
     */
    public String getApplicationInfo() {
        return String.format(
            "%s v%s - %s",
            appName,
            appVersion,
            appDescription
        );
    }

    /**
     * Gets server configuration
     *
     * @return server config
     */
    public String getServerConfig() {
        return String.format(
            "Server running on port %d with context path: %s",
            serverPort,
            contextPath
        );
    }

    /**
     * Gets database configuration (masked password)
     *
     * @return database config
     */
    public String getDatabaseConfig() {
        return String.format(
            "Database: %s (User: %s, Driver: %s)",
            databaseUrl,
            databaseUsername,
            databaseDriverClassName
        );
    }

    /**
     * Gets feature flags status
     *
     * @return feature flags
     */
    public String getFeatureFlags() {
        return String.format(
            "Features - Email: %s, SMS: %s, Analytics: %s",
            emailEnabled ? "ENABLED" : "DISABLED",
            smsEnabled ? "ENABLED" : "DISABLED",
            analyticsEnabled ? "ENABLED" : "DISABLED"
        );
    }

    /**
     * Gets business configuration
     *
     * @return business config
     */
    public String getBusinessConfig() {
        return String.format(
            "Max Upload: %d bytes, Session Timeout: %d seconds, Allowed Origins: %s",
            maxUploadSize,
            sessionTimeout,
            String.join(", ", allowedOrigins)
        );
    }

    /**
     * Gets all configuration as formatted string
     *
     * @return all configuration
     */
    public String getAllConfiguration() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Application Configuration ===\n");
        sb.append(getApplicationInfo()).append("\n");
        sb.append(getServerConfig()).append("\n");
        sb.append(getDatabaseConfig()).append("\n");
        sb.append(getFeatureFlags()).append("\n");
        sb.append(getBusinessConfig()).append("\n");
        return sb.toString();
    }

    // Getters
    public String getAppName() { return appName; }
    public String getAppVersion() { return appVersion; }
    public String getAppDescription() { return appDescription; }
    public int getServerPort() { return serverPort; }
    public String getContextPath() { return contextPath; }
    public String getDatabaseUrl() { return databaseUrl; }
    public String getDatabaseUsername() { return databaseUsername; }
    public String getDatabaseDriverClassName() { return databaseDriverClassName; }
    public boolean isEmailEnabled() { return emailEnabled; }
    public boolean isSmsEnabled() { return smsEnabled; }
    public boolean isAnalyticsEnabled() { return analyticsEnabled; }
    public long getMaxUploadSize() { return maxUploadSize; }
    public int getSessionTimeout() { return sessionTimeout; }
    public String[] getAllowedOrigins() { return allowedOrigins; }

    /**
     * Note: Password getter should be avoided in production
     * Shown here for educational purposes only
     */
    public String getDatabasePassword() { return databasePassword; }
}
