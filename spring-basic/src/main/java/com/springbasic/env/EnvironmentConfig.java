package com.springbasic.env;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Environment Variables Configuration
 *
 * This class demonstrates reading environment variables in Spring Boot.
 * Environment variables are useful for:
 * - Deployment-specific configuration
 * - Sensitive data (passwords, API keys)
 * - Cloud platform configuration
 * - CI/CD pipelines
 * - Container orchestration (Docker, Kubernetes)
 *
 * Spring Boot property resolution order:
 * 1. Command line arguments
 * 2. SPRING_APPLICATION_JSON
 * 3. ServletConfig/ServletContext init parameters
 * 4. JNDI attributes
 * 5. Java System properties (System.getProperties())
 * 6. OS environment variables
 * 7. Profile-specific application properties
 * 8. application.properties/yml
 * 9. @PropertySource annotations
 * 10. Default properties
 *
 * Environment variable naming conventions:
 * - Use UPPERCASE_WITH_UNDERSCORES
 * - Spring converts them to lowercase.with.dots
 * - Example: DATABASE_URL → database.url
 *
 * @author Spring Basic Tutorial
 */
@Component
public class EnvironmentConfig {

    private final Environment environment;

    // System environment variables
    @Value("${USER:unknown}")
    private String systemUser;

    @Value("${HOME:unknown}")
    private String homeDirectory;

    @Value("${PATH:unknown}")
    private String systemPath;

    // Custom environment variables (with defaults)
    @Value("${DATABASE_URL:jdbc:mysql://localhost:3306/defaultdb}")
    private String databaseUrl;

    @Value("${DATABASE_USERNAME:root}")
    private String databaseUsername;

    @Value("${DATABASE_PASSWORD:}")
    private String databasePassword;

    @Value("${API_KEY:demo-api-key-12345}")
    private String apiKey;

    @Value("${API_SECRET:demo-api-secret-67890}")
    private String apiSecret;

    @Value("${ENVIRONMENT:development}")
    private String environmentName;

    @Value("${LOG_LEVEL:INFO}")
    private String logLevel;

    @Value("${MAX_CONNECTIONS:10}")
    private int maxConnections;

    @Value("${ENABLE_CACHE:true}")
    private boolean cacheEnabled;

    @Value("${REDIS_HOST:localhost}")
    private String redisHost;

    @Value("${REDIS_PORT:6379}")
    private int redisPort;

    @Value("${AWS_REGION:us-east-1}")
    private String awsRegion;

    @Value("${AWS_ACCESS_KEY_ID:}")
    private String awsAccessKeyId;

    @Value("${AWS_SECRET_ACCESS_KEY:}")
    private String awsSecretAccessKey;

    /**
     * Constructor with Environment injection
     *
     * @param environment Spring Environment
     */
    public EnvironmentConfig(Environment environment) {
        this.environment = environment;
        System.out.println("EnvironmentConfig created - Reading environment variables");
        System.out.println("Environment: " + environmentName);
        System.out.println("Log Level: " + logLevel);
    }

    /**
     * Gets system environment information
     *
     * @return system environment info
     */
    public String getSystemEnvironmentInfo() {
        return String.format(
            "System - User: %s, Home: %s",
            systemUser,
            homeDirectory
        );
    }

    /**
     * Gets database configuration from environment variables
     *
     * @return database config
     */
    public String getDatabaseConfig() {
        return String.format(
            "Database - URL: %s, Username: %s, Password: %s",
            databaseUrl,
            databaseUsername,
            databasePassword.isEmpty() ? "(not set)" : "********"
        );
    }

    /**
     * Gets API credentials configuration
     * Note: In production, never log actual credentials
     *
     * @return API config (masked)
     */
    public String getApiConfig() {
        return String.format(
            "API - Key: %s..., Secret: %s...",
            apiKey.substring(0, Math.min(10, apiKey.length())),
            apiSecret.substring(0, Math.min(10, apiSecret.length()))
        );
    }

    /**
     * Gets application environment configuration
     *
     * @return environment config
     */
    public String getEnvironmentConfig() {
        return String.format(
            "Environment: %s, Log Level: %s, Max Connections: %d, Cache: %s",
            environmentName,
            logLevel,
            maxConnections,
            cacheEnabled ? "ENABLED" : "DISABLED"
        );
    }

    /**
     * Gets Redis configuration
     *
     * @return Redis config
     */
    public String getRedisConfig() {
        return String.format(
            "Redis - Host: %s, Port: %d",
            redisHost,
            redisPort
        );
    }

    /**
     * Gets AWS configuration
     *
     * @return AWS config (masked)
     */
    public String getAwsConfig() {
        return String.format(
            "AWS - Region: %s, Access Key: %s, Secret Key: %s",
            awsRegion,
            awsAccessKeyId.isEmpty() ? "(not set)" : awsAccessKeyId.substring(0, Math.min(8, awsAccessKeyId.length())) + "...",
            awsSecretAccessKey.isEmpty() ? "(not set)" : "********"
        );
    }

    /**
     * Gets all environment variables as a map
     * Note: Sensitive values are masked
     *
     * @return all environment variables
     */
    public Map<String, String> getAllEnvironmentVariables() {
        Map<String, String> envVars = new HashMap<>();

        // System variables
        envVars.put("USER", systemUser);
        envVars.put("HOME", homeDirectory);
        envVars.put("PATH", systemPath.substring(0, Math.min(50, systemPath.length())) + "...");

        // Application variables
        envVars.put("ENVIRONMENT", environmentName);
        envVars.put("LOG_LEVEL", logLevel);
        envVars.put("MAX_CONNECTIONS", String.valueOf(maxConnections));
        envVars.put("ENABLE_CACHE", String.valueOf(cacheEnabled));

        // Database variables (masked password)
        envVars.put("DATABASE_URL", databaseUrl);
        envVars.put("DATABASE_USERNAME", databaseUsername);
        envVars.put("DATABASE_PASSWORD", databasePassword.isEmpty() ? "(not set)" : "********");

        // API variables (masked)
        envVars.put("API_KEY", apiKey.substring(0, Math.min(10, apiKey.length())) + "...");
        envVars.put("API_SECRET", "********");

        // Redis variables
        envVars.put("REDIS_HOST", redisHost);
        envVars.put("REDIS_PORT", String.valueOf(redisPort));

        // AWS variables (masked)
        envVars.put("AWS_REGION", awsRegion);
        envVars.put("AWS_ACCESS_KEY_ID", awsAccessKeyId.isEmpty() ? "(not set)" : awsAccessKeyId.substring(0, Math.min(8, awsAccessKeyId.length())) + "...");
        envVars.put("AWS_SECRET_ACCESS_KEY", awsSecretAccessKey.isEmpty() ? "(not set)" : "********");

        return envVars;
    }

    /**
     * Demonstrates using Environment bean to access variables programmatically
     *
     * @param variableName name of environment variable
     * @return variable value or null
     */
    public String getEnvironmentVariable(String variableName) {
        return environment.getProperty(variableName);
    }

    /**
     * Checks if running in production environment
     *
     * @return true if production, false otherwise
     */
    public boolean isProduction() {
        return "production".equalsIgnoreCase(environmentName);
    }

    /**
     * Checks if running in development environment
     *
     * @return true if development, false otherwise
     */
    public boolean isDevelopment() {
        return "development".equalsIgnoreCase(environmentName);
    }

    /**
     * Gets active Spring profiles
     *
     * @return active profiles
     */
    public String[] getActiveProfiles() {
        return environment.getActiveProfiles();
    }

    /**
     * Gets complete environment summary
     *
     * @return environment summary
     */
    public String getEnvironmentSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Environment Configuration ===\n");
        sb.append(getSystemEnvironmentInfo()).append("\n");
        sb.append(getEnvironmentConfig()).append("\n");
        sb.append(getDatabaseConfig()).append("\n");
        sb.append(getApiConfig()).append("\n");
        sb.append(getRedisConfig()).append("\n");
        sb.append(getAwsConfig()).append("\n");
        sb.append(String.format("Active Profiles: %s\n", String.join(", ", getActiveProfiles())));
        return sb.toString();
    }

    // Getters
    public String getSystemUser() { return systemUser; }
    public String getHomeDirectory() { return homeDirectory; }
    public String getDatabaseUrl() { return databaseUrl; }
    public String getDatabaseUsername() { return databaseUsername; }
    public String getApiKey() { return apiKey; }
    public String getEnvironmentName() { return environmentName; }
    public String getLogLevel() { return logLevel; }
    public int getMaxConnections() { return maxConnections; }
    public boolean isCacheEnabled() { return cacheEnabled; }
    public String getRedisHost() { return redisHost; }
    public int getRedisPort() { return redisPort; }
    public String getAwsRegion() { return awsRegion; }
}
