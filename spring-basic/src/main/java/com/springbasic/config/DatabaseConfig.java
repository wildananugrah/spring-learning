package com.springbasic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Database Configuration Class
 *
 * This class demonstrates creating beans with configuration from application.properties.
 * It shows how to:
 * - Read configuration values
 * - Create beans with those values
 * - Organize related configuration
 * - Validate configuration
 *
 * In a real application, this would configure actual database connections.
 * For this tutorial, we create simple demonstration beans.
 *
 * @author Spring Basic Tutorial
 */
@Configuration
public class DatabaseConfig {

    @Value("${app.database.url:jdbc:mysql://localhost:3306/defaultdb}")
    private String databaseUrl;

    @Value("${app.database.username:root}")
    private String databaseUsername;

    @Value("${app.database.password:}")
    private String databasePassword;

    @Value("${app.database.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String databaseDriverClassName;

    @Value("${app.database.pool.min-size:5}")
    private int poolMinSize;

    @Value("${app.database.pool.max-size:20}")
    private int poolMaxSize;

    @Value("${app.database.pool.timeout:30000}")
    private long poolTimeout;

    /**
     * Constructor
     */
    public DatabaseConfig() {
        System.out.println("DatabaseConfig created - Will configure database beans");
    }

    /**
     * Creates a DatabaseProperties bean
     * This demonstrates grouping related configuration
     *
     * @return DatabaseProperties instance
     */
    @Bean
    public DatabaseProperties databaseProperties() {
        System.out.println("Creating DatabaseProperties bean with values from application.properties");

        DatabaseProperties props = new DatabaseProperties();
        props.setUrl(databaseUrl);
        props.setUsername(databaseUsername);
        props.setPassword(databasePassword);
        props.setDriverClassName(databaseDriverClassName);

        // Validate configuration
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            throw new IllegalStateException("Database URL cannot be empty");
        }

        return props;
    }

    /**
     * Creates a ConnectionPoolConfig bean
     * This demonstrates configuration for connection pooling
     *
     * @return ConnectionPoolConfig instance
     */
    @Bean
    public ConnectionPoolConfig connectionPoolConfig() {
        System.out.println("Creating ConnectionPoolConfig bean");

        ConnectionPoolConfig config = new ConnectionPoolConfig();
        config.setMinSize(poolMinSize);
        config.setMaxSize(poolMaxSize);
        config.setTimeout(poolTimeout);

        // Validate pool configuration
        if (poolMinSize > poolMaxSize) {
            throw new IllegalStateException("Pool min size cannot be greater than max size");
        }

        return config;
    }

    /**
     * Creates a DatabaseConnectionManager bean
     * This demonstrates a bean that uses other configuration beans
     *
     * @param dbProperties database properties
     * @param poolConfig connection pool configuration
     * @return DatabaseConnectionManager instance
     */
    @Bean
    public DatabaseConnectionManager databaseConnectionManager(
            DatabaseProperties dbProperties,
            ConnectionPoolConfig poolConfig) {

        System.out.println("Creating DatabaseConnectionManager with injected configuration");

        DatabaseConnectionManager manager = new DatabaseConnectionManager();
        manager.setDatabaseProperties(dbProperties);
        manager.setPoolConfig(poolConfig);
        manager.initialize();

        return manager;
    }

    /**
     * DatabaseProperties class
     */
    public static class DatabaseProperties {
        private String url;
        private String username;
        private String password;
        private String driverClassName;

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getDriverClassName() { return driverClassName; }
        public void setDriverClassName(String driverClassName) { this.driverClassName = driverClassName; }

        @Override
        public String toString() {
            return String.format("DatabaseProperties{url='%s', username='%s', driver='%s'}",
                url, username, driverClassName);
        }
    }

    /**
     * ConnectionPoolConfig class
     */
    public static class ConnectionPoolConfig {
        private int minSize;
        private int maxSize;
        private long timeout;

        public int getMinSize() { return minSize; }
        public void setMinSize(int minSize) { this.minSize = minSize; }
        public int getMaxSize() { return maxSize; }
        public void setMaxSize(int maxSize) { this.maxSize = maxSize; }
        public long getTimeout() { return timeout; }
        public void setTimeout(long timeout) { this.timeout = timeout; }

        @Override
        public String toString() {
            return String.format("ConnectionPoolConfig{min=%d, max=%d, timeout=%dms}",
                minSize, maxSize, timeout);
        }
    }

    /**
     * DatabaseConnectionManager class
     */
    public static class DatabaseConnectionManager {
        private DatabaseProperties databaseProperties;
        private ConnectionPoolConfig poolConfig;
        private boolean initialized = false;
        private int activeConnections = 0;

        public void setDatabaseProperties(DatabaseProperties props) {
            this.databaseProperties = props;
        }

        public void setPoolConfig(ConnectionPoolConfig config) {
            this.poolConfig = config;
        }

        public void initialize() {
            System.out.println("Initializing DatabaseConnectionManager...");
            System.out.println("Database: " + databaseProperties);
            System.out.println("Pool: " + poolConfig);
            this.initialized = true;
        }

        public String connect() {
            if (!initialized) {
                return "ERROR: Connection manager not initialized";
            }

            if (activeConnections >= poolConfig.getMaxSize()) {
                return "ERROR: Connection pool exhausted";
            }

            activeConnections++;
            return String.format(
                "Connected to %s (Active connections: %d/%d)",
                databaseProperties.getUrl(),
                activeConnections,
                poolConfig.getMaxSize()
            );
        }

        public String disconnect() {
            if (activeConnections > 0) {
                activeConnections--;
                return String.format("Disconnected (Active connections: %d)", activeConnections);
            }
            return "No active connections to disconnect";
        }

        public String getStatus() {
            return String.format(
                "DatabaseConnectionManager [Initialized: %s, Active: %d/%d, Min: %d]",
                initialized,
                activeConnections,
                poolConfig.getMaxSize(),
                poolConfig.getMinSize()
            );
        }

        public boolean isInitialized() { return initialized; }
        public int getActiveConnections() { return activeConnections; }
        public DatabaseProperties getDatabaseProperties() { return databaseProperties; }
        public ConnectionPoolConfig getPoolConfig() { return poolConfig; }
    }
}
