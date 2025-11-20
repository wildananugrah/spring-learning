package com.springbasic.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Spring Configuration Class
 *
 * This class demonstrates how to create beans using @Bean annotation
 * within a @Configuration class.
 *
 * Key points:
 * - @Configuration indicates this class contains bean definitions
 * - @Bean methods return objects that Spring will manage
 * - Bean names default to method names (can be customized)
 * - You can specify scope, lazy initialization, and dependencies
 *
 * @author Spring Basic Tutorial
 */
@Configuration
public class AppConfig {

    /**
     * Creates a singleton DataSource bean
     * This demonstrates creating a bean from a class you don't control
     * or when you need custom initialization logic
     *
     * @return DataSource instance
     */
    @Bean
    public DataSource dataSource() {
        System.out.println("Creating DataSource bean...");
        DataSource ds = new DataSource();
        ds.setUrl("jdbc:mysql://localhost:3306/mydb");
        ds.setUsername("root");
        ds.setPassword("password");
        return ds;
    }

    /**
     * Creates a ConnectionPool bean that depends on DataSource
     * This demonstrates bean dependency injection in @Bean methods
     *
     * @param dataSource the DataSource bean (injected by Spring)
     * @return ConnectionPool instance
     */
    @Bean
    public ConnectionPool connectionPool(DataSource dataSource) {
        System.out.println("Creating ConnectionPool bean...");
        ConnectionPool pool = new ConnectionPool();
        pool.setDataSource(dataSource);
        pool.setMaxConnections(10);
        pool.setMinConnections(2);
        return pool;
    }

    /**
     * Creates a prototype-scoped bean
     * A new instance is created each time this bean is requested
     *
     * @return new MessageGenerator instance
     */
    @Bean
    @Scope("prototype")
    public MessageGenerator messageGenerator() {
        System.out.println("Creating MessageGenerator bean (prototype scope)...");
        return new MessageGenerator();
    }

    /**
     * Creates a custom-named bean
     * Bean name is "customEmail" instead of default "customEmailService"
     *
     * @return EmailService instance
     */
    @Bean(name = "customEmail")
    public EmailService customEmailService() {
        System.out.println("Creating custom EmailService bean...");
        EmailService emailService = new EmailService();
        emailService.setSmtpHost("smtp.gmail.com");
        emailService.setSmtpPort(587);
        return emailService;
    }

    /**
     * Simple data class representing a DataSource configuration
     */
    public static class DataSource {
        private String url;
        private String username;
        private String password;

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        @Override
        public String toString() {
            return "DataSource{url='" + url + "', username='" + username + "'}";
        }
    }

    /**
     * Connection pool configuration class
     */
    public static class ConnectionPool {
        private DataSource dataSource;
        private int maxConnections;
        private int minConnections;

        public DataSource getDataSource() { return dataSource; }
        public void setDataSource(DataSource dataSource) { this.dataSource = dataSource; }
        public int getMaxConnections() { return maxConnections; }
        public void setMaxConnections(int maxConnections) { this.maxConnections = maxConnections; }
        public int getMinConnections() { return minConnections; }
        public void setMinConnections(int minConnections) { this.minConnections = minConnections; }

        @Override
        public String toString() {
            return "ConnectionPool{maxConnections=" + maxConnections +
                   ", minConnections=" + minConnections +
                   ", dataSource=" + dataSource + "}";
        }
    }

    /**
     * Message generator class (prototype scoped)
     */
    public static class MessageGenerator {
        private final long instanceId = System.currentTimeMillis();

        public String generate(String message) {
            return String.format("[Instance %d] Generated: %s", instanceId, message);
        }

        public long getInstanceId() {
            return instanceId;
        }
    }
}
