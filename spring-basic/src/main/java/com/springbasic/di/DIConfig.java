package com.springbasic.di;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Dependency Injection Configuration
 *
 * This configuration class demonstrates advanced DI concepts:
 * - Creating beans with dependencies
 * - Bean method parameter injection
 * - Conditional bean creation
 *
 * Spring automatically injects dependencies into @Bean method parameters.
 * This is another form of dependency injection at the configuration level.
 *
 * @author Spring Basic Tutorial
 */
@Configuration
public class DIConfig {

    /**
     * Creates a ReportService bean with injected dependencies
     *
     * Spring automatically injects the MessageService parameter
     * (will be the @Primary implementation - EmailMessageService)
     *
     * @param messageService injected by Spring
     * @return ReportService instance
     */
    @Bean
    public ReportService reportService(MessageService messageService) {
        System.out.println("Creating ReportService with injected MessageService: " +
            messageService.getServiceType());
        return new ReportService(messageService);
    }

    /**
     * ReportService demonstrates dependency injection in @Bean methods
     */
    public static class ReportService {
        private final MessageService messageService;
        private int reportsGenerated = 0;

        public ReportService(MessageService messageService) {
            this.messageService = messageService;
        }

        public String generateAndSendReport(String recipient, String reportType) {
            reportsGenerated++;
            String reportContent = String.format(
                "Report #%d - Type: %s, Generated at: %d",
                reportsGenerated,
                reportType,
                System.currentTimeMillis()
            );

            return messageService.sendMessage(recipient, reportContent);
        }

        public int getReportsGenerated() {
            return reportsGenerated;
        }

        public String getMessageServiceType() {
            return messageService.getServiceType();
        }
    }
}
