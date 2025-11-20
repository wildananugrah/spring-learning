package com.springbasic.annotations;

import org.springframework.stereotype.Component;

/**
 * @Component Annotation Example
 *
 * @Component is a generic stereotype annotation for any Spring-managed component.
 * It indicates that the class is a "component" and will be auto-detected through
 * classpath scanning.
 *
 * Key characteristics:
 * - Generic stereotype for any Spring-managed bean
 * - Auto-detected through component scanning
 * - Default bean name is class name with first letter lowercase
 * - Can specify custom bean name: @Component("customName")
 * - Base annotation for @Service, @Repository, and @Controller
 *
 * When to use:
 * - General-purpose Spring components
 * - When more specific stereotypes don't fit
 * - Utility classes, helpers, factories
 *
 * @author Spring Basic Tutorial
 */
@Component
public class ComponentExample {

    private int operationCount = 0;

    /**
     * Constructor
     */
    public ComponentExample() {
        System.out.println("ComponentExample bean created");
    }

    /**
     * Performs a generic operation
     *
     * @param data input data
     * @return processed result
     */
    public String performOperation(String data) {
        operationCount++;
        return String.format(
            "[ComponentExample] Processing #%d: %s (HashCode: %d)",
            operationCount,
            data,
            this.hashCode()
        );
    }

    /**
     * Gets operation count
     *
     * @return number of operations performed
     */
    public int getOperationCount() {
        return operationCount;
    }

    /**
     * Gets component information
     *
     * @return component info
     */
    public String getComponentInfo() {
        return String.format(
            "ComponentExample - Operations: %d, HashCode: %d",
            operationCount,
            this.hashCode()
        );
    }
}
