package com.springbasic.di;

import org.springframework.stereotype.Service;

/**
 * SMS Implementation of MessageService
 *
 * This class demonstrates:
 * - Alternative implementation of an interface
 * - Can be injected using @Qualifier("smsMessageService")
 * - Not marked as @Primary, so it won't be the default choice
 *
 * To inject this specific implementation, use:
 * @Qualifier("smsMessageService") or @Qualifier("SMS")
 *
 * @author Spring Basic Tutorial
 */
@Service("smsMessageService")  // Explicit bean name
public class SmsMessageService implements MessageService {

    private int messagesSent = 0;

    /**
     * Constructor
     */
    public SmsMessageService() {
        System.out.println("SmsMessageService created");
    }

    /**
     * Sends a message via SMS
     *
     * @param recipient the phone number recipient
     * @param message the message content
     * @return delivery result
     */
    @Override
    public String sendMessage(String recipient, String message) {
        messagesSent++;

        // Simulate SMS character limit
        String truncatedMessage = message.length() > 160
            ? message.substring(0, 157) + "..."
            : message;

        String result = String.format(
            "[SMS] Sending to %s: %s (Total SMS sent: %d)",
            recipient,
            truncatedMessage,
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
        return "SMS";
    }

    /**
     * Checks if SMS service is available
     *
     * @return true (SMS service is always available in this demo)
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
