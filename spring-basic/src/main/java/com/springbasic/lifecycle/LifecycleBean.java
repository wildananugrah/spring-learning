package com.springbasic.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates Spring Bean Lifecycle with all callback methods
 *
 * Bean Lifecycle Order:
 * 1. Constructor
 * 2. Dependency Injection
 * 3. @PostConstruct
 * 4. InitializingBean.afterPropertiesSet()
 * 5. Custom init method (via @Bean(initMethod))
 * 6. Bean is ready for use
 * 7. Container shutdown begins
 * 8. @PreDestroy
 * 9. DisposableBean.destroy()
 * 10. Custom destroy method (via @Bean(destroyMethod))
 *
 * @author Spring Basic Tutorial
 */
@Component
@Getter
public class LifecycleBean implements InitializingBean, DisposableBean {

    private final List<String> lifecycleEvents = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime initializedAt;
    private LocalDateTime destroyedAt;
    private boolean isInitialized = false;

    /**
     * 1. Constructor - First step in bean lifecycle
     * Called by Spring when creating the bean instance
     */
    public LifecycleBean() {
        createdAt = LocalDateTime.now();
        addEvent("1. Constructor called");
        System.out.println("LifecycleBean: Constructor called at " + createdAt);
    }

    /**
     * 3. @PostConstruct - Called after dependency injection
     * This is the recommended way to initialize beans
     */
    @PostConstruct
    public void postConstruct() {
        addEvent("3. @PostConstruct called");
        System.out.println("LifecycleBean: @PostConstruct called");
    }

    /**
     * 4. InitializingBean.afterPropertiesSet() - Called after @PostConstruct
     * Alternative to @PostConstruct (using interface)
     */
    @Override
    public void afterPropertiesSet() {
        initializedAt = LocalDateTime.now();
        isInitialized = true;
        addEvent("4. InitializingBean.afterPropertiesSet() called");
        System.out.println("LifecycleBean: afterPropertiesSet called at " + initializedAt);
    }

    /**
     * Custom initialization method (would be called if configured via @Bean(initMethod))
     */
    public void customInit() {
        addEvent("5. Custom init method called");
        System.out.println("LifecycleBean: Custom init method called");
    }

    /**
     * Business method - Bean is ready for use
     */
    public String doWork(String task) {
        String result = "Processing task: " + task;
        addEvent("Bean is working: " + task);
        return result;
    }

    /**
     * 8. @PreDestroy - Called before bean destruction
     * Cleanup resources here
     */
    @PreDestroy
    public void preDestroy() {
        addEvent("8. @PreDestroy called");
        System.out.println("LifecycleBean: @PreDestroy called - cleaning up resources");
    }

    /**
     * 9. DisposableBean.destroy() - Called after @PreDestroy
     * Alternative to @PreDestroy (using interface)
     */
    @Override
    public void destroy() {
        destroyedAt = LocalDateTime.now();
        addEvent("9. DisposableBean.destroy() called");
        System.out.println("LifecycleBean: destroy called at " + destroyedAt);
    }

    /**
     * Custom destroy method (would be called if configured via @Bean(destroyMethod))
     */
    public void customDestroy() {
        addEvent("10. Custom destroy method called");
        System.out.println("LifecycleBean: Custom destroy method called");
    }

    /**
     * Utility method to track lifecycle events
     */
    private void addEvent(String event) {
        String timestamp = LocalDateTime.now().toString();
        lifecycleEvents.add(timestamp + " - " + event);
    }

    /**
     * Get all lifecycle events that occurred
     */
    public List<String> getLifecycleEvents() {
        return new ArrayList<>(lifecycleEvents);
    }

    /**
     * Get bean information
     */
    public String getBeanInfo() {
        return String.format(
            "LifecycleBean[created=%s, initialized=%s, isReady=%s, eventsCount=%d]",
            createdAt, initializedAt, isInitialized, lifecycleEvents.size()
        );
    }
}
