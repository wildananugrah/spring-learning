package com.springbasic.lifecycle;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates custom init and destroy methods configured via @Bean
 *
 * This bean doesn't use @PostConstruct/@PreDestroy or implement interfaces.
 * Instead, it uses custom methods configured in LifecycleConfig.
 *
 * @author Spring Basic Tutorial
 */
@Getter
public class CustomLifecycleBean {

    private final List<String> lifecycleEvents = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime initializedAt;
    private boolean isReady = false;

    public CustomLifecycleBean() {
        createdAt = LocalDateTime.now();
        addEvent("Constructor called");
        System.out.println("CustomLifecycleBean: Constructor called");
    }

    /**
     * Custom initialization method
     * Configured via @Bean(initMethod = "init")
     */
    public void init() {
        initializedAt = LocalDateTime.now();
        isReady = true;
        addEvent("Custom init() method called");
        System.out.println("CustomLifecycleBean: init() called - bean is now ready");
    }

    /**
     * Business method
     */
    public String processData(String data) {
        if (!isReady) {
            throw new IllegalStateException("Bean not initialized!");
        }
        addEvent("Processing data: " + data);
        return "Processed: " + data;
    }

    /**
     * Custom cleanup method
     * Configured via @Bean(destroyMethod = "cleanup")
     */
    public void cleanup() {
        addEvent("Custom cleanup() method called");
        System.out.println("CustomLifecycleBean: cleanup() called - releasing resources");
        isReady = false;
    }

    private void addEvent(String event) {
        String timestamp = LocalDateTime.now().toString();
        lifecycleEvents.add(timestamp + " - " + event);
    }

    public String getBeanInfo() {
        return String.format(
            "CustomLifecycleBean[created=%s, initialized=%s, isReady=%s]",
            createdAt, initializedAt, isReady
        );
    }
}
