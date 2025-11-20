package com.springbasic.beans;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller to demonstrate Spring Beans and Bean Scopes
 *
 * This controller exposes REST endpoints to demonstrate:
 * 1. Different ways to create beans (@Component vs @Bean)
 * 2. Bean scopes (singleton, prototype, request, session)
 * 3. Bean lifecycle and behavior
 *
 * Endpoints:
 * - GET /api/beans/component - Demonstrates @Component bean
 * - GET /api/beans/configuration - Demonstrates @Bean from @Configuration
 * - GET /api/beans/scopes/singleton - Demonstrates singleton scope
 * - GET /api/beans/scopes/prototype - Demonstrates prototype scope
 * - GET /api/beans/scopes/request - Demonstrates request scope
 * - GET /api/beans/scopes/session - Demonstrates session scope
 * - POST /api/beans/email - Sends email using EmailService bean
 *
 * @author Spring Basic Tutorial
 */
@RestController
@RequestMapping("/api/beans")
@RequiredArgsConstructor
public class BeanDemoController {

    private final UserService userService;
    private final EmailService emailService;
    private final ApplicationContext applicationContext;
    private final BeanScopesDemo.SingletonBean singletonBean;
    private final BeanScopesDemo.RequestBean requestBean;
    private final BeanScopesDemo.SessionBean sessionBean;

    /**
     * Demonstrates @Component bean (UserService)
     * Shows that UserService is automatically detected and created by Spring
     *
     * @return user service demonstration
     */
    @GetMapping("/component")
    public Map<String, Object> demonstrateComponentBean() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "Bean created with @Component annotation");
        response.put("beanType", "UserService");
        response.put("beanHashCode", userService.hashCode());
        response.put("users", userService.getAllUsers());
        response.put("explanation", "UserService is auto-detected by component scanning and " +
            "registered as a singleton bean. Spring creates and manages its lifecycle.");

        return response;
    }

    /**
     * Demonstrates @Bean from @Configuration (EmailService)
     * Shows beans created through configuration class
     *
     * @return email service demonstration
     */
    @GetMapping("/configuration")
    public Map<String, Object> demonstrateConfigurationBean() {
        Map<String, Object> response = new HashMap<>();
        response.put("description", "Bean created with @Bean in @Configuration class");
        response.put("beanType", "EmailService");
        response.put("beanHashCode", emailService.hashCode());
        response.put("serviceInfo", emailService.getServiceInfo());
        response.put("explanation", "EmailService is created in AppConfig class using @Bean method. " +
            "This approach gives more control over bean creation and initialization.");

        // Also demonstrate other beans from AppConfig
        AppConfig.DataSource dataSource = applicationContext.getBean(AppConfig.DataSource.class);
        AppConfig.ConnectionPool connectionPool = applicationContext.getBean(AppConfig.ConnectionPool.class);

        response.put("dataSource", dataSource.toString());
        response.put("connectionPool", connectionPool.toString());

        return response;
    }

    /**
     * Demonstrates singleton scope
     * Same instance is returned for all requests
     *
     * @return singleton scope demonstration
     */
    @GetMapping("/scopes/singleton")
    public Map<String, Object> demonstrateSingletonScope() {
        String access1 = singletonBean.access();
        String access2 = singletonBean.access();

        // Get bean again from context to prove it's the same instance
        BeanScopesDemo.SingletonBean beanFromContext =
            applicationContext.getBean(BeanScopesDemo.SingletonBean.class);
        String access3 = beanFromContext.access();

        Map<String, Object> response = new HashMap<>();
        response.put("scope", "SINGLETON");
        response.put("description", "One instance per Spring IoC container");
        response.put("access1", access1);
        response.put("access2", access2);
        response.put("access3", access3);
        response.put("injectedBeanHashCode", singletonBean.hashCode());
        response.put("contextBeanHashCode", beanFromContext.hashCode());
        response.put("areSameInstance", singletonBean == beanFromContext);
        response.put("totalAccesses", singletonBean.getAccessCount());
        response.put("explanation", "All accesses use the same instance. Access count increases " +
            "because the same bean is reused.");

        return response;
    }

    /**
     * Demonstrates prototype scope
     * New instance is created each time bean is requested
     *
     * @return prototype scope demonstration
     */
    @GetMapping("/scopes/prototype")
    public Map<String, Object> demonstratePrototypeScope() {
        // Get three different instances
        BeanScopesDemo.PrototypeBean bean1 =
            applicationContext.getBean(BeanScopesDemo.PrototypeBean.class);
        BeanScopesDemo.PrototypeBean bean2 =
            applicationContext.getBean(BeanScopesDemo.PrototypeBean.class);
        BeanScopesDemo.PrototypeBean bean3 =
            applicationContext.getBean(BeanScopesDemo.PrototypeBean.class);

        String access1 = bean1.access();
        String access2 = bean2.access();
        String access3 = bean3.access();

        Map<String, Object> response = new HashMap<>();
        response.put("scope", "PROTOTYPE");
        response.put("description", "New instance created every time bean is requested");
        response.put("access1", access1);
        response.put("access2", access2);
        response.put("access3", access3);
        response.put("bean1HashCode", bean1.hashCode());
        response.put("bean2HashCode", bean2.hashCode());
        response.put("bean3HashCode", bean3.hashCode());
        response.put("areDifferentInstances", bean1 != bean2 && bean2 != bean3);
        response.put("explanation", "Each bean request creates a new instance. HashCodes are " +
            "different, and each has its own state.");

        return response;
    }

    /**
     * Demonstrates request scope
     * One instance per HTTP request
     *
     * @return request scope demonstration
     */
    @GetMapping("/scopes/request")
    public Map<String, Object> demonstrateRequestScope() {
        String access1 = requestBean.access();
        String access2 = requestBean.access();
        String access3 = requestBean.access();

        Map<String, Object> response = new HashMap<>();
        response.put("scope", "REQUEST");
        response.put("description", "One instance per HTTP request");
        response.put("access1", access1);
        response.put("access2", access2);
        response.put("access3", access3);
        response.put("beanHashCode", requestBean.hashCode());
        response.put("totalAccesses", requestBean.getAccessCount());
        response.put("explanation", "Within a single HTTP request, the same instance is used. " +
            "Access count increases. A new request will get a new instance.");
        response.put("note", "Try making multiple requests - each will have a different hashCode " +
            "and start with access count = 1");

        return response;
    }

    /**
     * Demonstrates session scope
     * One instance per HTTP session
     *
     * @return session scope demonstration
     */
    @GetMapping("/scopes/session")
    public Map<String, Object> demonstrateSessionScope() {
        String access1 = sessionBean.access();
        String access2 = sessionBean.access();

        Map<String, Object> response = new HashMap<>();
        response.put("scope", "SESSION");
        response.put("description", "One instance per HTTP session");
        response.put("access1", access1);
        response.put("access2", access2);
        response.put("beanHashCode", sessionBean.hashCode());
        response.put("totalAccesses", sessionBean.getAccessCount());
        response.put("sessionData", sessionBean.getSessionData());
        response.put("explanation", "Within the same session, the same instance is used. " +
            "Access count persists across requests in the same session.");
        response.put("note", "Use different browsers or incognito mode to create different sessions");

        return response;
    }

    /**
     * Updates session data
     *
     * @param data new session data
     * @return updated session information
     */
    @PostMapping("/scopes/session/data")
    public Map<String, Object> updateSessionData(@RequestBody Map<String, String> data) {
        String sessionData = data.getOrDefault("data", "Updated session data");
        sessionBean.setSessionData(sessionData);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Session data updated");
        response.put("sessionData", sessionBean.getSessionData());
        response.put("beanHashCode", sessionBean.hashCode());
        response.put("explanation", "Data persists in the session-scoped bean for this session");

        return response;
    }

    /**
     * Sends an email using the EmailService bean
     *
     * @param emailRequest email details
     * @return email send result
     */
    @PostMapping("/email")
    public Map<String, Object> sendEmail(@RequestBody EmailRequest emailRequest) {
        EmailService.EmailResult result = emailService.sendEmail(
            emailRequest.getTo(),
            emailRequest.getSubject(),
            emailRequest.getBody()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("result", result);
        response.put("serviceInfo", emailService.getServiceInfo());
        response.put("beanHashCode", emailService.hashCode());

        return response;
    }

    /**
     * Email request data class
     */
    @lombok.Data
    public static class EmailRequest {
        private String to;
        private String subject;
        private String body;
    }
}
