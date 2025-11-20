package com.springbasic.env;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller to demonstrate Environment Variables
 *
 * This controller exposes REST endpoints to demonstrate:
 * 1. Reading system environment variables
 * 2. Reading custom environment variables
 * 3. Using environment variables for configuration
 * 4. Environment-based conditional logic
 * 5. Best practices for environment variables
 *
 * Endpoints:
 * - GET /api/env/system - Shows system environment variables
 * - GET /api/env/application - Shows application environment variables
 * - GET /api/env/database - Shows database configuration from env vars
 * - GET /api/env/all - Shows all environment configuration
 * - GET /api/env/variable/{name} - Gets specific environment variable
 * - GET /api/env/profiles - Shows active Spring profiles
 *
 * @author Spring Basic Tutorial
 */
@RestController
@RequestMapping("/api/env")
@RequiredArgsConstructor
public class EnvDemoController {

    private final EnvironmentConfig environmentConfig;
    private final Environment environment;

    /**
     * Shows system environment variables
     *
     * @return system environment info
     */
    @GetMapping("/system")
    public Map<String, Object> getSystemEnvironment() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "System environment variables");
        response.put("info", environmentConfig.getSystemEnvironmentInfo());

        Map<String, String> systemVars = new HashMap<>();
        systemVars.put("USER", environmentConfig.getSystemUser());
        systemVars.put("HOME", environmentConfig.getHomeDirectory());
        systemVars.put("JAVA_HOME", environment.getProperty("JAVA_HOME", "not set"));
        systemVars.put("OS", System.getProperty("os.name"));
        systemVars.put("OS_VERSION", System.getProperty("os.version"));
        systemVars.put("OS_ARCH", System.getProperty("os.arch"));
        systemVars.put("JAVA_VERSION", System.getProperty("java.version"));

        response.put("systemVariables", systemVars);
        response.put("note", "These are standard system environment variables");

        return response;
    }

    /**
     * Shows application environment variables
     *
     * @return application environment info
     */
    @GetMapping("/application")
    public Map<String, Object> getApplicationEnvironment() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "Application environment configuration");
        response.put("summary", environmentConfig.getEnvironmentConfig());

        Map<String, Object> appVars = new HashMap<>();
        appVars.put("ENVIRONMENT", environmentConfig.getEnvironmentName());
        appVars.put("LOG_LEVEL", environmentConfig.getLogLevel());
        appVars.put("MAX_CONNECTIONS", environmentConfig.getMaxConnections());
        appVars.put("ENABLE_CACHE", environmentConfig.isCacheEnabled());
        appVars.put("isProduction", environmentConfig.isProduction());
        appVars.put("isDevelopment", environmentConfig.isDevelopment());

        response.put("applicationVariables", appVars);
        response.put("note", "These variables control application behavior");

        return response;
    }

    /**
     * Shows database configuration from environment variables
     *
     * @return database configuration
     */
    @GetMapping("/database")
    public Map<String, Object> getDatabaseEnvironment() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "Database configuration from environment variables");
        response.put("config", environmentConfig.getDatabaseConfig());

        Map<String, Object> dbVars = new HashMap<>();
        dbVars.put("DATABASE_URL", environmentConfig.getDatabaseUrl());
        dbVars.put("DATABASE_USERNAME", environmentConfig.getDatabaseUsername());
        dbVars.put("DATABASE_PASSWORD", "(masked for security)");

        response.put("databaseVariables", dbVars);
        response.put("note", "Sensitive data like passwords should always be in environment variables, not in code");

        return response;
    }

    /**
     * Shows API configuration from environment variables
     *
     * @return API configuration
     */
    @GetMapping("/api")
    public Map<String, Object> getApiEnvironment() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "API credentials from environment variables");
        response.put("config", environmentConfig.getApiConfig());

        Map<String, String> apiVars = new HashMap<>();
        apiVars.put("API_KEY", "(masked - first 10 chars shown in config)");
        apiVars.put("API_SECRET", "(masked for security)");

        response.put("apiVariables", apiVars);
        response.put("warning", "Never expose API keys or secrets in responses. This is for demonstration only.");
        response.put("bestPractice", "Use secret management systems like AWS Secrets Manager, HashiCorp Vault, etc.");

        return response;
    }

    /**
     * Shows Redis configuration from environment variables
     *
     * @return Redis configuration
     */
    @GetMapping("/redis")
    public Map<String, Object> getRedisEnvironment() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "Redis configuration from environment variables");
        response.put("config", environmentConfig.getRedisConfig());

        Map<String, Object> redisVars = new HashMap<>();
        redisVars.put("REDIS_HOST", environmentConfig.getRedisHost());
        redisVars.put("REDIS_PORT", environmentConfig.getRedisPort());

        response.put("redisVariables", redisVars);
        response.put("useCase", "Cache configuration via environment variables");

        return response;
    }

    /**
     * Shows AWS configuration from environment variables
     *
     * @return AWS configuration
     */
    @GetMapping("/aws")
    public Map<String, Object> getAwsEnvironment() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "AWS configuration from environment variables");
        response.put("config", environmentConfig.getAwsConfig());

        Map<String, Object> awsVars = new HashMap<>();
        awsVars.put("AWS_REGION", environmentConfig.getAwsRegion());
        awsVars.put("AWS_ACCESS_KEY_ID", "(masked for security)");
        awsVars.put("AWS_SECRET_ACCESS_KEY", "(masked for security)");

        response.put("awsVariables", awsVars);
        response.put("note", "AWS credentials should be managed via IAM roles in production");

        return response;
    }

    /**
     * Shows all environment configuration
     *
     * @return all environment configuration
     */
    @GetMapping("/all")
    public Map<String, Object> getAllEnvironment() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "Complete environment configuration");
        response.put("summary", environmentConfig.getEnvironmentSummary());
        response.put("allVariables", environmentConfig.getAllEnvironmentVariables());
        response.put("activeProfiles", environmentConfig.getActiveProfiles());

        return response;
    }

    /**
     * Gets a specific environment variable by name
     *
     * @param name variable name
     * @return variable value
     */
    @GetMapping("/variable/{name}")
    public Map<String, Object> getSpecificVariable(@PathVariable String name) {
        String value = environmentConfig.getEnvironmentVariable(name);

        Map<String, Object> response = new HashMap<>();
        response.put("variableName", name);
        response.put("value", value != null ? value : "(not set)");
        response.put("exists", value != null);

        if (value == null) {
            response.put("note", "Variable not found in environment");
        }

        return response;
    }

    /**
     * Shows active Spring profiles
     *
     * @return active profiles information
     */
    @GetMapping("/profiles")
    public Map<String, Object> getProfiles() {
        String[] activeProfiles = environmentConfig.getActiveProfiles();
        String[] defaultProfiles = environment.getDefaultProfiles();

        Map<String, Object> response = new HashMap<>();
        response.put("description", "Spring profile configuration");
        response.put("activeProfiles", activeProfiles.length > 0 ? activeProfiles : new String[]{"(none)"});
        response.put("defaultProfiles", defaultProfiles);
        response.put("explanation", "Profiles allow different configurations for different environments");

        Map<String, String> examples = new HashMap<>();
        examples.put("development", "Local development with debug logging, embedded database");
        examples.put("staging", "Pre-production environment with test data");
        examples.put("production", "Live production environment with optimized settings");

        response.put("commonProfiles", examples);
        response.put("howToSet", "Use SPRING_PROFILES_ACTIVE environment variable or --spring.profiles.active=dev");

        return response;
    }

    /**
     * Demonstrates environment-based conditional logic
     *
     * @return conditional behavior based on environment
     */
    @GetMapping("/conditional")
    public Map<String, Object> demonstrateConditionalLogic() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "Conditional behavior based on environment");

        // Different behavior based on environment
        String behavior;
        if (environmentConfig.isProduction()) {
            behavior = "Production mode: Logging minimized, caching enabled, strict error handling";
        } else if (environmentConfig.isDevelopment()) {
            behavior = "Development mode: Verbose logging, no caching, detailed error messages";
        } else {
            behavior = "Unknown environment: Using default settings";
        }

        response.put("currentEnvironment", environmentConfig.getEnvironmentName());
        response.put("behavior", behavior);
        response.put("cacheEnabled", environmentConfig.isCacheEnabled());
        response.put("logLevel", environmentConfig.getLogLevel());

        return response;
    }

    /**
     * Shows best practices for environment variables
     *
     * @return best practices information
     */
    @GetMapping("/best-practices")
    public Map<String, Object> getBestPractices() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "Best practices for environment variables");

        String[] bestPractices = {
            "Never commit sensitive data (passwords, API keys) to version control",
            "Use environment variables for deployment-specific configuration",
            "Provide sensible defaults for non-sensitive values",
            "Use UPPERCASE_WITH_UNDERSCORES naming convention",
            "Document required environment variables in README",
            "Use secret management systems for production (AWS Secrets Manager, Vault)",
            "Validate required environment variables at startup",
            "Use .env files for local development (add to .gitignore)",
            "Use different values for different environments (dev, staging, prod)",
            "Never log sensitive environment variable values"
        };

        Map<String, String> examples = new HashMap<>();
        examples.put("Good", "DATABASE_PASSWORD as environment variable");
        examples.put("Bad", "database.password=secret123 in application.properties committed to Git");

        Map<String, String> tools = new HashMap<>();
        tools.put("Local Development", ".env files with dotenv library");
        tools.put("Docker", "Environment variables in docker-compose.yml or Dockerfile");
        tools.put("Kubernetes", "ConfigMaps for config, Secrets for sensitive data");
        tools.put("Cloud Platforms", "Heroku Config Vars, AWS Systems Manager Parameter Store");
        tools.put("CI/CD", "GitHub Secrets, GitLab CI/CD Variables");

        response.put("bestPractices", bestPractices);
        response.put("examples", examples);
        response.put("tools", tools);

        return response;
    }

    /**
     * Simulates setting an environment variable (educational only)
     * Note: You cannot actually set environment variables at runtime in Java
     *
     * @param request variable name and value
     * @return educational response
     */
    @PostMapping("/set")
    public Map<String, Object> setEnvironmentVariable(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String value = request.get("value");

        Map<String, Object> response = new HashMap<>();
        response.put("warning", "Environment variables cannot be set at runtime in Java");
        response.put("requestedName", name);
        response.put("requestedValue", value);
        response.put("howToSet", new String[]{
            "1. Set before starting application: export " + name + "=" + value,
            "2. In IDE: Configure run configuration environment variables",
            "3. In Docker: ENV " + name + " " + value + " in Dockerfile",
            "4. In Kubernetes: Add to ConfigMap or Secret",
            "5. In cloud platforms: Use platform-specific environment variable settings"
        });

        return response;
    }
}
