package com.springbasic.annotations;

import org.springframework.stereotype.Service;

/**
 * @Service Annotation Example
 *
 * @Service is a specialization of @Component annotation.
 * It indicates that the class holds business logic.
 *
 * Key characteristics:
 * - Semantic specialization of @Component
 * - Indicates business logic layer
 * - Auto-detected through component scanning
 * - May have additional semantics in the future
 * - Makes code more readable and maintainable
 *
 * When to use:
 * - Business logic layer
 * - Service classes that implement business operations
 * - Classes that coordinate between repository and controller
 *
 * Technical note:
 * Currently, @Service is functionally identical to @Component.
 * However, it's a best practice to use @Service for service layer classes
 * as it clearly indicates the role and may enable additional features in the future.
 *
 * @author Spring Basic Tutorial
 */
@Service
public class ServiceExample {

    private int transactionCount = 0;

    /**
     * Constructor
     */
    public ServiceExample() {
        System.out.println("ServiceExample bean created");
    }

    /**
     * Performs a business transaction
     *
     * @param amount transaction amount
     * @param description transaction description
     * @return transaction result
     */
    public String performTransaction(double amount, String description) {
        transactionCount++;

        // Simulate business logic
        if (amount <= 0) {
            return String.format("[FAILED] Transaction #%d: Invalid amount %.2f", transactionCount, amount);
        }

        return String.format(
            "[SUCCESS] Transaction #%d: %.2f - %s (HashCode: %d)",
            transactionCount,
            amount,
            description,
            this.hashCode()
        );
    }

    /**
     * Calculates total with tax
     * Example of business logic method
     *
     * @param subtotal subtotal amount
     * @param taxRate tax rate (e.g., 0.1 for 10%)
     * @return total amount with tax
     */
    public double calculateTotalWithTax(double subtotal, double taxRate) {
        double tax = subtotal * taxRate;
        double total = subtotal + tax;

        System.out.printf("Business Calculation: Subtotal: %.2f, Tax (%.1f%%): %.2f, Total: %.2f%n",
            subtotal, taxRate * 100, tax, total);

        return total;
    }

    /**
     * Gets transaction count
     *
     * @return number of transactions performed
     */
    public int getTransactionCount() {
        return transactionCount;
    }

    /**
     * Gets service information
     *
     * @return service info
     */
    public String getServiceInfo() {
        return String.format(
            "ServiceExample - Transactions: %d, HashCode: %d, Annotation: @Service",
            transactionCount,
            this.hashCode()
        );
    }
}
