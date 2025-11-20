package com.springbasic.di;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * NotificationService - Demonstrates Constructor Injection
 *
 * Constructor injection is the RECOMMENDED approach for dependency injection in Spring.
 *
 * Advantages:
 * - Dependencies are required and cannot be null
 * - Immutable (final fields)
 * - Easy to test (can instantiate without Spring)
 * - Makes dependencies explicit
 * - Prevents circular dependencies
 *
 * @RequiredArgsConstructor from Lombok automatically generates a constructor
 * with parameters for all final fields, making the code cleaner.
 *
 * @author Spring Basic Tutorial
 */
@Service
@RequiredArgsConstructor  // Generates constructor for final fields
public class NotificationService {

    // Constructor injection - dependencies are final and required
    private final MessageService primaryMessageService;  // Injects @Primary implementation (Email)

    @Qualifier("smsMessageService")
    private final MessageService smsService;  // Injects specific implementation using @Qualifier

    private final List<String> notificationHistory = new ArrayList<>();

    /**
     * Sends a notification using the primary message service (Email)
     *
     * @param recipient the recipient
     * @param message the message
     * @return notification result
     */
    public String sendNotification(String recipient, String message) {
        String result = primaryMessageService.sendMessage(recipient, message);
        notificationHistory.add(result);
        return result;
    }

    /**
     * Sends a notification using SMS service
     *
     * @param recipient the phone number
     * @param message the message
     * @return notification result
     */
    public String sendSmsNotification(String recipient, String message) {
        String result = smsService.sendMessage(recipient, message);
        notificationHistory.add(result);
        return result;
    }

    /**
     * Sends notification via both email and SMS
     *
     * @param recipient the recipient
     * @param message the message
     * @return combined result
     */
    public String sendMultiChannelNotification(String recipient, String message) {
        String emailResult = primaryMessageService.sendMessage(recipient, message);
        String smsResult = smsService.sendMessage(recipient, message);

        notificationHistory.add(emailResult);
        notificationHistory.add(smsResult);

        return String.format("Email: %s | SMS: %s", emailResult, smsResult);
    }

    /**
     * Gets the notification history
     *
     * @return list of all notifications sent
     */
    public List<String> getNotificationHistory() {
        return new ArrayList<>(notificationHistory);
    }

    /**
     * Gets service information
     *
     * @return service info
     */
    public String getServiceInfo() {
        return String.format(
            "NotificationService [Primary: %s, Secondary: %s, History size: %d]",
            primaryMessageService.getServiceType(),
            smsService.getServiceType(),
            notificationHistory.size()
        );
    }

    /**
     * Clears notification history
     */
    public void clearHistory() {
        notificationHistory.clear();
    }
}
