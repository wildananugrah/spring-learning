package com.springbasic.singleton;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller to demonstrate Singleton Pattern
 *
 * This controller exposes REST endpoints to demonstrate both:
 * 1. Traditional singleton pattern (DatabaseConnection)
 * 2. Spring-managed singleton bean (SingletonService)
 *
 * Endpoints:
 * - GET /api/singleton/traditional - Demonstrates traditional singleton
 * - GET /api/singleton/spring - Demonstrates Spring singleton bean
 * - GET /api/singleton/compare - Compares both approaches
 * - POST /api/singleton/process - Processes a request using singleton service
 *
 * @author Spring Basic Tutorial
 */
@RestController
@RequestMapping("/api/singleton")
@RequiredArgsConstructor
public class SingletonDemoController {

    private final SingletonService singletonService;

    /**
     * Demonstrates traditional singleton pattern
     * Each call uses the same DatabaseConnection instance
     *
     * @return information about the traditional singleton
     */
    @GetMapping("/traditional")
    public Map<String, Object> demonstrateTraditionalSingleton() {
        DatabaseConnection db1 = DatabaseConnection.getInstance();
        DatabaseConnection db2 = DatabaseConnection.getInstance();

        Map<String, Object> response = new HashMap<>();
        response.put("description", "Traditional Singleton Pattern");
        response.put("instance1HashCode", db1.hashCode());
        response.put("instance2HashCode", db2.hashCode());
        response.put("areSameInstance", db1 == db2);
        response.put("connection1", db1.connect());
        response.put("connection2", db2.connect());
        response.put("totalConnections", db1.getConnectionCount());
        response.put("explanation", "Both instances have the same hashCode, proving they are the same object");

        return response;
    }

    /**
     * Demonstrates Spring singleton bean
     * Spring injects the same instance everywhere
     *
     * @return information about the Spring singleton
     */
    @GetMapping("/spring")
    public Map<String, Object> demonstrateSprintSingleton() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "Spring Singleton Bean");
        response.put("instanceInfo", singletonService.getInstanceInfo());
        response.put("hashCode", singletonService.hashCode());
        response.put("requestCount", singletonService.getRequestCount());
        response.put("creationTime", singletonService.getCreationTime());
        response.put("explanation", "Spring creates only one instance of this bean. " +
                "The request count increases with each call, proving the same instance is reused.");

        return response;
    }

    /**
     * Compares traditional singleton vs Spring singleton
     *
     * @return comparison between both approaches
     */
    @GetMapping("/compare")
    public Map<String, Object> compareSingletons() {
        DatabaseConnection db = DatabaseConnection.getInstance();

        Map<String, Object> traditional = new HashMap<>();
        traditional.put("type", "Traditional Singleton");
        traditional.put("hashCode", db.hashCode());
        traditional.put("managed", "Self-managed");
        traditional.put("testing", "Difficult to test/mock");
        traditional.put("lifecycle", "Application lifetime");

        Map<String, Object> spring = new HashMap<>();
        spring.put("type", "Spring Singleton");
        spring.put("hashCode", singletonService.hashCode());
        spring.put("managed", "Spring Container");
        spring.put("testing", "Easy to test/mock");
        spring.put("lifecycle", "Spring context lifetime");

        Map<String, Object> response = new HashMap<>();
        response.put("traditional", traditional);
        response.put("spring", spring);
        response.put("recommendation", "Use Spring-managed singletons for better testability and lifecycle management");

        return response;
    }

    /**
     * Processes a request using the singleton service
     *
     * @param message the message to process
     * @return processed result
     */
    @PostMapping("/process")
    public Map<String, Object> processRequest(@RequestBody Map<String, String> payload) {
        String message = payload.getOrDefault("message", "Default message");
        String result = singletonService.processRequest(message);

        Map<String, Object> response = new HashMap<>();
        response.put("result", result);
        response.put("totalRequests", singletonService.getRequestCount());
        response.put("instanceHashCode", singletonService.hashCode());

        return response;
    }

    /**
     * Resets the singleton service (for demonstration purposes only)
     * Note: In real applications, you typically wouldn't reset singleton state
     *
     * @return reset confirmation
     */
    @PostMapping("/reset")
    public Map<String, String> resetSingleton() {
        singletonService.setRequestCount(0);
        return Map.of(
            "message", "Singleton service reset",
            "note", "This is for demonstration only. In production, avoid resetting singleton state."
        );
    }
}
