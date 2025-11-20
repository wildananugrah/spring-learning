package com.springbasic.di;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Email Implementation of MessageService
 *
 * This class demonstrates:
 * - Interface implementation for dependency injection
 * - @Primary annotation to mark this as the default implementation
 * - @Service stereotype annotation for automatic bean detection
 *
 * @Primary annotation makes this implementation the default choice when
 * multiple implementations of MessageService exist and no @Qualifier is specified.
 *
 * @author Spring Basic Tutorial
 */
@Service
@Primary  // This will be the default implementation when injecting MessageService
public class EmailMessageService implements MessageService {

    private int messagesSent = 0;

    /**
     * Constructor
     */
    public EmailMessageService() {
        System.out.println("EmailMessageService created (PRIMARY implementation)");
    }

    /**
     * Sends a message via email
     *
     * @param recipient the email recipient
     * @param message the message content
     * @return delivery result
     */
    @Override
    public String sendMessage(String recipient, String message) {
        messagesSent++;
        String result = String.format(
            "[EMAIL] Sending to %s: %s (Total emails sent: %d)",
            recipient,
            message,
            messagesSent
        );
        System.out.println(result);
        return result;
    }

    /**
     * Gets the service type
     *
     * @return service type identifier
     */
    @Override
    public String getServiceType() {
        return "EMAIL";
    }

    /**
     * Checks if email service is available
     *
     * @return true (email service is always available in this demo)
     */
    @Override
    public boolean isAvailable() {
        return true;
    }

    /**
     * Gets the number of messages sent
     *
     * @return message count
     */
    public int getMessagesSent() {
        return messagesSent;
    }
}
