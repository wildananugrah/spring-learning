package com.springbasic.di;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * OrderService - Demonstrates Setter Injection
 *
 * Setter injection uses @Autowired on setter methods to inject dependencies.
 *
 * Use cases for setter injection:
 * - Optional dependencies (may or may not be available)
 * - Dependencies that can be changed after object creation
 * - Reconfiguration scenarios
 * - Legacy code compatibility
 *
 * Advantages:
 * - Dependencies are optional
 * - Can change dependencies after construction
 * - Useful for optional features
 *
 * Disadvantages:
 * - Dependencies can be null
 * - Cannot use final fields
 * - Object may be in incomplete state after construction
 * - Less explicit than constructor injection
 *
 * For most cases, constructor injection is preferred. Use setter injection
 * only when you truly need optional or mutable dependencies.
 *
 * @author Spring Basic Tutorial
 */
@Service
@Data
public class OrderService {

    // Dependencies are not final because they're set via setters
    private MessageService emailService;
    private MessageService smsService;

    private final List<Order> orders = new ArrayList<>();
    private int orderIdCounter = 1;

    /**
     * Default constructor
     * Object can be created without dependencies (they're optional)
     */
    public OrderService() {
        System.out.println("OrderService created (dependencies will be injected via setters)");
    }

    /**
     * Setter injection for email service
     * @Autowired tells Spring to call this setter with the appropriate bean
     *
     * @param emailService the email message service
     */
    @Autowired
    public void setEmailService(MessageService emailService) {
        System.out.println("Injecting emailService via setter");
        this.emailService = emailService;
    }

    /**
     * Setter injection for SMS service with @Qualifier
     * Demonstrates injecting a specific implementation
     *
     * @param smsService the SMS message service
     */
    @Autowired
    @Qualifier("smsMessageService")
    public void setSmsService(MessageService smsService) {
        System.out.println("Injecting smsService via setter");
        this.smsService = smsService;
    }

    /**
     * Creates an order and sends confirmation
     *
     * @param customerEmail customer email
     * @param productName product name
     * @param quantity quantity ordered
     * @return order details
     */
    public Order createOrder(String customerEmail, String productName, int quantity) {
        Order order = new Order(orderIdCounter++, customerEmail, productName, quantity);
        orders.add(order);

        // Send confirmation via email if service is available
        if (emailService != null) {
            emailService.sendMessage(
                customerEmail,
                String.format("Order #%d confirmed: %d x %s", order.getId(), quantity, productName)
            );
        }

        return order;
    }

    /**
     * Creates an order with both email and SMS notifications
     *
     * @param customerEmail customer email
     * @param customerPhone customer phone
     * @param productName product name
     * @param quantity quantity ordered
     * @return order details
     */
    public Order createOrderWithSmsNotification(String customerEmail, String customerPhone,
                                                 String productName, int quantity) {
        Order order = new Order(orderIdCounter++, customerEmail, productName, quantity);
        orders.add(order);

        String message = String.format("Order #%d confirmed: %d x %s", order.getId(), quantity, productName);

        // Send via email if available
        if (emailService != null) {
            emailService.sendMessage(customerEmail, message);
        }

        // Send via SMS if available
        if (smsService != null) {
            smsService.sendMessage(customerPhone, message);
        }

        return order;
    }

    /**
     * Gets all orders
     *
     * @return list of orders
     */
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    /**
     * Gets service information
     *
     * @return service info
     */
    public String getServiceInfo() {
        return String.format(
            "OrderService [Email: %s, SMS: %s, Total orders: %d]",
            emailService != null ? emailService.getServiceType() : "NOT INJECTED",
            smsService != null ? smsService.getServiceType() : "NOT INJECTED",
            orders.size()
        );
    }

    /**
     * Order data class
     */
    @Data
    public static class Order {
        private final int id;
        private final String customerEmail;
        private final String productName;
        private final int quantity;
        private final long timestamp;

        public Order(int id, String customerEmail, String productName, int quantity) {
            this.id = id;
            this.customerEmail = customerEmail;
            this.productName = productName;
            this.quantity = quantity;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
