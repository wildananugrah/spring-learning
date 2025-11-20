package com.springbasic.di;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * UserController - Demonstrates Field Injection
 *
 * Field injection uses @Autowired annotation directly on fields.
 *
 * While it's the simplest approach, it has several DISADVANTAGES:
 * - Cannot create immutable fields (no final)
 * - Dependencies can be null
 * - Harder to test (requires Spring context or reflection)
 * - Hides dependencies (not visible in constructor)
 * - Can lead to circular dependencies
 *
 * Field injection is NOT RECOMMENDED for production code.
 * Use constructor injection instead.
 *
 * This class is included for educational purposes to show all DI types.
 *
 * @author Spring Basic Tutorial
 */
@Controller
@RequestMapping("/api/di/field")
public class UserController {

    // Field injection - NOT RECOMMENDED but commonly seen in legacy code
    @Autowired
    private MessageService messageService;  // Injects @Primary implementation (Email)

    @Autowired
    private NotificationService notificationService;

    /**
     * Note: No constructor needed with field injection
     * Spring uses reflection to inject dependencies directly into fields
     */

    /**
     * Sends a welcome message using field-injected service
     *
     * @return welcome message result
     */
    @GetMapping("/welcome")
    @ResponseBody
    public Map<String, Object> sendWelcomeMessage() {
        String result = messageService.sendMessage(
            "user@example.com",
            "Welcome to our application!"
        );

        Map<String, Object> response = new HashMap<>();
        response.put("injectionType", "FIELD INJECTION");
        response.put("result", result);
        response.put("serviceType", messageService.getServiceType());
        response.put("warning", "Field injection is not recommended. Use constructor injection instead.");
        response.put("reasons", new String[]{
            "Cannot use final fields (immutability)",
            "Dependencies can be null",
            "Harder to test without Spring container",
            "Hidden dependencies",
            "Potential circular dependency issues"
        });

        return response;
    }

    /**
     * Demonstrates field injection with multiple dependencies
     *
     * @return service information
     */
    @GetMapping("/info")
    @ResponseBody
    public Map<String, Object> getServiceInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("injectionType", "FIELD INJECTION");
        response.put("messageServiceType", messageService.getServiceType());
        response.put("messageServiceAvailable", messageService.isAvailable());
        response.put("notificationServiceInfo", notificationService.getServiceInfo());
        response.put("recommendation", "Refactor to use constructor injection for better code quality");

        return response;
    }

    /**
     * Example of why field injection is problematic for testing
     * Without Spring, you cannot easily set these dependencies
     */
    public void exampleTestingChallenge() {
        // If you want to test this class without Spring, you would need to:
        // 1. Use reflection to set the fields, OR
        // 2. Add setter methods (which defeats encapsulation), OR
        // 3. Use Spring test context (slower tests)
        //
        // With constructor injection, you could simply:
        // UserController controller = new UserController(mockMessageService, mockNotificationService);
    }
}
