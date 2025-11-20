package com.springbasic.annotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Qualifier Annotation Example
 *
 * @Qualifier is used to resolve ambiguity when multiple beans of the same type exist.
 * It allows you to specify which bean should be injected by name or qualifier value.
 *
 * Key characteristics:
 * - Resolves autowiring ambiguity
 * - Can be used with @Autowired
 * - Can specify bean name or custom qualifier
 * - Used on fields, parameters, or methods
 * - Works with @Primary (Qualifier takes precedence)
 *
 * When to use:
 * - Multiple implementations of same interface
 * - Multiple beans of same type
 * - Need to inject specific bean
 * - Fine-grained control over injection
 *
 * Alternatives:
 * - @Primary: marks default bean
 * - Bean name matching
 * - Custom qualifier annotations
 *
 * @author Spring Basic Tutorial
 */
@Component
public class QualifierExample {

    // Inject specific payment processor using @Qualifier
    private final PaymentProcessor creditCardProcessor;
    private final PaymentProcessor paypalProcessor;
    private final PaymentProcessor bitcoinProcessor;

    /**
     * Constructor with @Qualifier to specify which beans to inject
     *
     * @param creditCardProcessor credit card payment processor
     * @param paypalProcessor PayPal payment processor
     * @param bitcoinProcessor Bitcoin payment processor
     */
    @Autowired
    public QualifierExample(
            @Qualifier("creditCardProcessor") PaymentProcessor creditCardProcessor,
            @Qualifier("paypalProcessor") PaymentProcessor paypalProcessor,
            @Qualifier("bitcoinProcessor") PaymentProcessor bitcoinProcessor) {

        this.creditCardProcessor = creditCardProcessor;
        this.paypalProcessor = paypalProcessor;
        this.bitcoinProcessor = bitcoinProcessor;

        System.out.println("QualifierExample created with qualified payment processors");
    }

    /**
     * Processes payment using specified payment method
     *
     * @param paymentMethod payment method (creditcard, paypal, bitcoin)
     * @param amount payment amount
     * @return payment result
     */
    public String processPayment(String paymentMethod, double amount) {
        PaymentProcessor processor = switch (paymentMethod.toLowerCase()) {
            case "creditcard" -> creditCardProcessor;
            case "paypal" -> paypalProcessor;
            case "bitcoin" -> bitcoinProcessor;
            default -> throw new IllegalArgumentException("Unknown payment method: " + paymentMethod);
        };

        return processor.processPayment(amount);
    }

    /**
     * Demonstrates all payment processors
     *
     * @param amount test amount
     * @return results from all processors
     */
    public String demonstrateAllProcessors(double amount) {
        StringBuilder result = new StringBuilder();
        result.append("=== @Qualifier Demo - Multiple Payment Processors ===\n");
        result.append(creditCardProcessor.processPayment(amount)).append("\n");
        result.append(paypalProcessor.processPayment(amount)).append("\n");
        result.append(bitcoinProcessor.processPayment(amount)).append("\n");
        return result.toString();
    }

    /**
     * Gets information about injected processors
     *
     * @return processor information
     */
    public String getProcessorInfo() {
        return String.format(
            "Processors: CreditCard=%s, PayPal=%s, Bitcoin=%s",
            creditCardProcessor.getProcessorName(),
            paypalProcessor.getProcessorName(),
            bitcoinProcessor.getProcessorName()
        );
    }

    /**
     * PaymentProcessor interface
     */
    public interface PaymentProcessor {
        String processPayment(double amount);
        String getProcessorName();
    }

    /**
     * Configuration class to create multiple PaymentProcessor beans
     */
    @Configuration
    public static class PaymentProcessorConfig {

        @Bean("creditCardProcessor")
        public PaymentProcessor creditCardProcessor() {
            return new PaymentProcessor() {
                private int transactionCount = 0;

                @Override
                public String processPayment(double amount) {
                    transactionCount++;
                    return String.format(
                        "[CREDIT CARD] Transaction #%d: $%.2f (Fee: $%.2f)",
                        transactionCount,
                        amount,
                        amount * 0.029  // 2.9% fee
                    );
                }

                @Override
                public String getProcessorName() {
                    return "Credit Card Processor";
                }
            };
        }

        @Bean("paypalProcessor")
        public PaymentProcessor paypalProcessor() {
            return new PaymentProcessor() {
                private int transactionCount = 0;

                @Override
                public String processPayment(double amount) {
                    transactionCount++;
                    return String.format(
                        "[PAYPAL] Transaction #%d: $%.2f (Fee: $%.2f)",
                        transactionCount,
                        amount,
                        amount * 0.034  // 3.4% fee
                    );
                }

                @Override
                public String getProcessorName() {
                    return "PayPal Processor";
                }
            };
        }

        @Bean("bitcoinProcessor")
        public PaymentProcessor bitcoinProcessor() {
            return new PaymentProcessor() {
                private int transactionCount = 0;

                @Override
                public String processPayment(double amount) {
                    transactionCount++;
                    double btcAmount = amount / 45000.0; // Simplified BTC conversion
                    return String.format(
                        "[BITCOIN] Transaction #%d: $%.2f (%.6f BTC)",
                        transactionCount,
                        amount,
                        btcAmount
                    );
                }

                @Override
                public String getProcessorName() {
                    return "Bitcoin Processor";
                }
            };
        }
    }
}
