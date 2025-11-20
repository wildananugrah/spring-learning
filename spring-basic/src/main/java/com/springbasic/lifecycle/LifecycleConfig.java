package com.springbasic.lifecycle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class demonstrating custom init and destroy methods
 *
 * @Bean annotation supports:
 * - initMethod: Method to call after bean creation
 * - destroyMethod: Method to call before bean destruction
 *
 * @author Spring Basic Tutorial
 */
@Configuration
public class LifecycleConfig {

    /**
     * Create bean with custom init and destroy methods
     *
     * Spring will call:
     * 1. Constructor
     * 2. Dependency injection (if any)
     * 3. init() method
     * ... bean is used ...
     * 4. cleanup() method (on shutdown)
     */
    @Bean(initMethod = "init", destroyMethod = "cleanup")
    public CustomLifecycleBean customLifecycleBean() {
        System.out.println("LifecycleConfig: Creating CustomLifecycleBean...");
        return new CustomLifecycleBean();
    }
}
