package com.springbasic.annotations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Configuration Annotation Example
 *
 * @Configuration indicates that the class contains Spring bean definitions.
 * It's a source of bean definitions and is processed by Spring to generate
 * bean definitions and service requests for those beans at runtime.
 *
 * Key characteristics:
 * - Indicates class contains @Bean methods
 * - Methods annotated with @Bean return objects managed by Spring
 * - Full @Configuration vs Lite @Bean modes
 * - Proxy-based to ensure singleton behavior
 * - Can import other configuration classes
 *
 * When to use:
 * - Creating beans from third-party libraries
 * - Complex bean initialization logic
 * - Conditional bean creation
 * - When you can't use @Component (external classes)
 *
 * @Configuration vs @Component:
 * - @Configuration is proxy-based (CGLIB)
 * - Ensures singleton behavior for @Bean methods
 * - @Bean methods can call each other safely
 *
 * @author Spring Basic Tutorial
 */
@Configuration
public class ConfigurationExample {

    /**
     * Creates a CacheManager bean
     * Demonstrates simple bean creation
     *
     * @return CacheManager instance
     */
    @Bean
    public CacheManager cacheManager() {
        System.out.println("Creating CacheManager bean via @Configuration");
        CacheManager manager = new CacheManager();
        manager.setMaxSize(1000);
        manager.setTimeout(3600);
        return manager;
    }

    /**
     * Creates a SecurityManager bean
     * Demonstrates bean with custom initialization
     *
     * @return SecurityManager instance
     */
    @Bean(name = "securityManager", initMethod = "init", destroyMethod = "cleanup")
    public SecurityManager securityManager() {
        System.out.println("Creating SecurityManager bean via @Configuration");
        SecurityManager manager = new SecurityManager();
        manager.setEncryptionEnabled(true);
        manager.setAlgorithm("AES-256");
        return manager;
    }

    /**
     * Creates a MonitoringService bean that depends on CacheManager
     * Demonstrates bean dependencies
     *
     * @param cacheManager injected by Spring
     * @return MonitoringService instance
     */
    @Bean
    public MonitoringService monitoringService(CacheManager cacheManager) {
        System.out.println("Creating MonitoringService bean with CacheManager dependency");
        MonitoringService service = new MonitoringService();
        service.setCacheManager(cacheManager);
        service.setMonitoringEnabled(true);
        return service;
    }

    /**
     * CacheManager class
     */
    public static class CacheManager {
        private int maxSize;
        private int timeout;
        private int cacheHits = 0;

        public void setMaxSize(int maxSize) { this.maxSize = maxSize; }
        public void setTimeout(int timeout) { this.timeout = timeout; }
        public int getMaxSize() { return maxSize; }
        public int getTimeout() { return timeout; }

        public String getCacheInfo() {
            return String.format("CacheManager [MaxSize: %d, Timeout: %d, Hits: %d]",
                maxSize, timeout, cacheHits);
        }

        public void recordHit() {
            cacheHits++;
        }
    }

    /**
     * SecurityManager class with lifecycle callbacks
     */
    public static class SecurityManager {
        private boolean encryptionEnabled;
        private String algorithm;
        private boolean initialized = false;

        public void setEncryptionEnabled(boolean enabled) { this.encryptionEnabled = enabled; }
        public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
        public boolean isEncryptionEnabled() { return encryptionEnabled; }
        public String getAlgorithm() { return algorithm; }
        public boolean isInitialized() { return initialized; }

        /**
         * Init method called after bean construction
         */
        public void init() {
            System.out.println("SecurityManager init method called");
            initialized = true;
        }

        /**
         * Cleanup method called before bean destruction
         */
        public void cleanup() {
            System.out.println("SecurityManager cleanup method called");
            initialized = false;
        }

        public String getSecurityInfo() {
            return String.format("SecurityManager [Encryption: %s, Algorithm: %s, Initialized: %s]",
                encryptionEnabled, algorithm, initialized);
        }

        public String encrypt(String data) {
            if (!initialized || !encryptionEnabled) {
                return data;
            }
            return String.format("[ENCRYPTED:%s] %s", algorithm, data);
        }
    }

    /**
     * MonitoringService class
     */
    public static class MonitoringService {
        private CacheManager cacheManager;
        private boolean monitoringEnabled;
        private int monitoringCount = 0;

        public void setCacheManager(CacheManager cacheManager) {
            this.cacheManager = cacheManager;
        }

        public void setMonitoringEnabled(boolean enabled) {
            this.monitoringEnabled = enabled;
        }

        public boolean isMonitoringEnabled() { return monitoringEnabled; }

        public String monitor(String event) {
            if (!monitoringEnabled) {
                return "Monitoring disabled";
            }

            monitoringCount++;
            cacheManager.recordHit();

            return String.format(
                "Event #%d monitored: %s [Cache: %s]",
                monitoringCount,
                event,
                cacheManager.getCacheInfo()
            );
        }

        public String getMonitoringInfo() {
            return String.format("MonitoringService [Enabled: %s, Events: %d]",
                monitoringEnabled, monitoringCount);
        }
    }
}
