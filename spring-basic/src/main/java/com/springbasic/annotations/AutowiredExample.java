package com.springbasic.annotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @Autowired Annotation Example
 *
 * @Autowired is used for automatic dependency injection.
 * Spring automatically resolves and injects collaborating beans.
 *
 * Key characteristics:
 * - Automatic dependency injection
 * - Can be used on: constructors, fields, setters, config methods
 * - Required by default (can be made optional with required=false)
 * - Supports injection of collections (List, Set, Map)
 * - Can inject Optional<T> for optional dependencies
 * - From Spring 4.3+, implicit on single constructor
 *
 * Injection types:
 * 1. Constructor injection (RECOMMENDED)
 * 2. Field injection (NOT RECOMMENDED)
 * 3. Setter injection (for optional dependencies)
 *
 * Best practices:
 * - Prefer constructor injection
 * - Use final fields for immutability
 * - Avoid field injection (harder to test)
 *
 * @author Spring Basic Tutorial
 */
@Component
public class AutowiredExample {

    // Field injection - NOT RECOMMENDED
    // Shown here for educational purposes only
    @Autowired
    private ComponentExample componentExample;

    // Optional dependency using Optional<T>
    @Autowired(required = false)
    private ServiceExample serviceExample;

    private RepositoryExample repositoryExample;

    /**
     * Constructor injection - RECOMMENDED
     * @Autowired is optional on single constructor (Spring 4.3+)
     *
     * @param repositoryExample injected by Spring
     */
    @Autowired  // Optional if there's only one constructor
    public AutowiredExample(RepositoryExample repositoryExample) {
        this.repositoryExample = repositoryExample;
        System.out.println("AutowiredExample created with constructor injection");
    }

    /**
     * Example of setter injection
     * Useful for optional dependencies or reconfiguration
     */
    private ComponentExample alternativeComponent;

    @Autowired(required = false)
    public void setAlternativeComponent(ComponentExample component) {
        this.alternativeComponent = component;
        System.out.println("Alternative component injected via setter");
    }

    /**
     * Demonstrates using injected dependencies
     *
     * @return result using all dependencies
     */
    public String useDependencies() {
        StringBuilder result = new StringBuilder();
        result.append("=== @Autowired Injection Demo ===\n");

        // Using field-injected dependency
        if (componentExample != null) {
            result.append("Component (field): ")
                .append(componentExample.getComponentInfo())
                .append("\n");
        }

        // Using optional dependency
        if (serviceExample != null) {
            result.append("Service (optional): ")
                .append(serviceExample.getServiceInfo())
                .append("\n");
        } else {
            result.append("Service: Not available (optional dependency)\n");
        }

        // Using constructor-injected dependency
        if (repositoryExample != null) {
            result.append("Repository (constructor): ")
                .append(repositoryExample.getRepositoryInfo())
                .append("\n");
        }

        // Using setter-injected dependency
        if (alternativeComponent != null) {
            result.append("Alternative Component (setter): ")
                .append(alternativeComponent.getComponentInfo())
                .append("\n");
        }

        return result.toString();
    }

    /**
     * Performs operations using dependencies
     *
     * @return operation result
     */
    public String performOperations() {
        StringBuilder result = new StringBuilder();

        // Use component
        String componentResult = componentExample.performOperation("Test data");
        result.append(componentResult).append("\n");

        // Use service if available
        if (serviceExample != null) {
            String serviceResult = serviceExample.performTransaction(100.0, "Test transaction");
            result.append(serviceResult).append("\n");
        }

        // Use repository
        long count = repositoryExample.count();
        result.append(String.format("Repository has %d products\n", count));

        return result.toString();
    }

    /**
     * Gets information about injection types
     *
     * @return injection info
     */
    public String getInjectionInfo() {
        return String.format(
            "AutowiredExample - Component: %s, Service: %s, Repository: %s",
            componentExample != null ? "injected" : "null",
            serviceExample != null ? "injected" : "null",
            repositoryExample != null ? "injected" : "null"
        );
    }

    // Getters for testing
    public ComponentExample getComponentExample() { return componentExample; }
    public ServiceExample getServiceExample() { return serviceExample; }
    public RepositoryExample getRepositoryExample() { return repositoryExample; }
}
