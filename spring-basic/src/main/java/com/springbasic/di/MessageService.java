package com.springbasic.di;

/**
 * Message Service Interface
 *
 * This interface demonstrates dependency injection through abstraction.
 * By coding to an interface instead of concrete implementation, we achieve:
 * - Loose coupling
 * - Easy testing (can mock implementations)
 * - Flexibility to switch implementations
 * - Better adherence to SOLID principles
 *
 * Spring can inject any implementation of this interface based on:
 * - @Primary annotation
 * - @Qualifier annotation
 * - Bean name matching
 * - Conditional bean creation
 *
 * @author Spring Basic Tutorial
 */
public interface MessageService {

    /**
     * Sends a message to a recipient
     *
     * @param recipient the message recipient
     * @param message the message content
     * @return delivery result
     */
    String sendMessage(String recipient, String message);

    /**
     * Gets the service type/name
     *
     * @return service type identifier
     */
    String getServiceType();

    /**
     * Checks if the service is available
     *
     * @return true if service is available, false otherwise
     */
    boolean isAvailable();
}
