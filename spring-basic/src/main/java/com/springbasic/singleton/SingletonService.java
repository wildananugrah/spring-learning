package com.springbasic.singleton;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

/**
 * Spring Singleton Bean Example
 *
 * In Spring, beans are singleton by default. This means Spring container
 * creates only one instance of the bean per Spring IoC container.
 *
 * This is different from the traditional singleton pattern as it's managed
 * by the Spring container, not by the class itself.
 *
 * Key differences from traditional singleton:
 * - No private constructor
 * - No static getInstance() method
 * - Spring manages the lifecycle
 * - Easier to test (can mock/replace)
 *
 * Note: We use @Getter/@Setter instead of @Data to avoid Lombok generating
 * a custom hashCode() that changes based on field values. We want to use
 * the default Object.hashCode() which is based on object identity.
 *
 * @author Spring Basic Tutorial
 */
@Service
@Getter
@Setter
public class SingletonService {

    private int requestCount = 0;
    private final long creationTime;

    /**
     * Constructor is called only once by Spring container
     */
    public SingletonService() {
        this.creationTime = System.currentTimeMillis();
        System.out.println("SingletonService bean created at: " + creationTime);
    }

    /**
     * Increments and returns the request count
     * This demonstrates that the same instance is used across requests
     *
     * @return current request count
     */
    public int incrementAndGetCount() {
        requestCount++;
        return requestCount;
    }

    /**
     * Gets information about this singleton instance
     *
     * @return instance information
     */
    public String getInstanceInfo() {
        return String.format(
            "SingletonService - Created at: %d, Request count: %d, HashCode: %d",
            creationTime,
            requestCount,
            this.hashCode()
        );
    }

    /**
     * Processes a request using the singleton service
     *
     * @param message the message to process
     * @return processed message
     */
    public String processRequest(String message) {
        incrementAndGetCount();
        return String.format("Request #%d: %s (Instance: %d)", requestCount, message, this.hashCode());
    }
}
