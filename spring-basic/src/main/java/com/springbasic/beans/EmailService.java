package com.springbasic.beans;

import lombok.Data;

/**
 * Bean created via @Configuration class (AppConfig)
 *
 * This class demonstrates a bean that is created and configured
 * through a @Bean method in a @Configuration class.
 *
 * Use this approach when:
 * - You need custom initialization logic
 * - The class is from a third-party library (you can't add @Component)
 * - You want to conditionally create beans
 * - You need to create multiple instances with different configurations
 *
 * Key differences from @Component:
 * - Not auto-detected by component scanning
 * - Explicitly created in @Configuration class
 * - More control over bean creation and initialization
 * - Can easily create multiple beans of same type with different configs
 *
 * @author Spring Basic Tutorial
 */
@Data
public class EmailService {

    private String smtpHost;
    private int smtpPort;
    private int emailsSent = 0;

    /**
     * Sends an email
     *
     * @param to recipient email address
     * @param subject email subject
     * @param body email body
     * @return email send result
     */
    public EmailResult sendEmail(String to, String subject, String body) {
        emailsSent++;
        System.out.printf("Sending email via %s:%d%n", smtpHost, smtpPort);
        System.out.printf("To: %s%nSubject: %s%nBody: %s%n", to, subject, body);

        return new EmailResult(
            true,
            "Email sent successfully via " + smtpHost,
            emailsSent
        );
    }

    /**
     * Gets email service information
     *
     * @return service information
     */
    public String getServiceInfo() {
        return String.format(
            "EmailService [SMTP: %s:%d, Emails sent: %d]",
            smtpHost,
            smtpPort,
            emailsSent
        );
    }

    /**
     * Email result data class
     */
    @Data
    public static class EmailResult {
        private final boolean success;
        private final String message;
        private final int totalEmailsSent;

        public EmailResult(boolean success, String message, int totalEmailsSent) {
            this.success = success;
            this.message = message;
            this.totalEmailsSent = totalEmailsSent;
        }
    }
}
