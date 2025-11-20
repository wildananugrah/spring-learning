package com.springbasic.beans;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Bean Scopes Demonstration
 *
 * Spring supports several bean scopes that control the lifecycle and visibility
 * of bean instances:
 *
 * 1. SINGLETON (default) - One instance per Spring IoC container
 * 2. PROTOTYPE - New instance every time bean is requested
 * 3. REQUEST - One instance per HTTP request (web applications)
 * 4. SESSION - One instance per HTTP session (web applications)
 * 5. APPLICATION - One instance per ServletContext (web applications)
 * 6. WEBSOCKET - One instance per WebSocket session
 *
 * This class demonstrates the most commonly used scopes.
 *
 * @author Spring Basic Tutorial
 */
@Configuration
public class BeanScopesDemo {

    /**
     * SINGLETON scope (default)
     * Only one instance exists in the Spring container
     * Same instance is returned for all requests
     */
    @Bean
    @Scope("singleton")  // This is the default, explicitly shown for clarity
    public SingletonBean singletonBean() {
        System.out.println("Creating SingletonBean...");
        return new SingletonBean();
    }

    /**
     * PROTOTYPE scope
     * New instance is created every time the bean is requested
     * Spring doesn't manage the complete lifecycle of prototype beans
     */
    @Bean
    @Scope("prototype")
    public PrototypeBean prototypeBean() {
        System.out.println("Creating PrototypeBean...");
        return new PrototypeBean();
    }

    /**
     * REQUEST scope
     * One instance per HTTP request
     * Only available in web-aware Spring ApplicationContext
     * proxyMode = TARGET_CLASS creates a CGLIB proxy to inject into singletons
     */
    @Bean
    @RequestScope  // Equivalent to @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public RequestBean requestBean() {
        System.out.println("Creating RequestBean...");
        return new RequestBean();
    }

    /**
     * SESSION scope
     * One instance per HTTP session
     * Only available in web-aware Spring ApplicationContext
     * Instance lives as long as the HTTP session is active
     */
    @Bean
    @SessionScope  // Equivalent to @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public SessionBean sessionBean() {
        System.out.println("Creating SessionBean...");
        return new SessionBean();
    }

    /**
     * Singleton bean class
     *
     * Note: We use @Getter/@Setter instead of @Data to avoid Lombok generating
     * a custom hashCode() that changes based on field values (like accessCount).
     * We want to use the default Object.hashCode() which is based on object identity,
     * so the hashCode remains constant for the same singleton instance.
     */
    @Getter
    @Setter
    public static class SingletonBean {
        private final long creationTime = System.currentTimeMillis();
        private int accessCount = 0;

        public String access() {
            accessCount++;
            return String.format("SingletonBean [Created: %d, Accesses: %d, HashCode: %d]",
                creationTime, accessCount, this.hashCode());
        }
    }

    /**
     * Prototype bean class
     *
     * Note: Using @Getter/@Setter instead of @Data to preserve object identity hashCode.
     * For prototype beans, each instance will have a DIFFERENT hashCode (different objects),
     * but the hashCode won't change when accessCount changes.
     */
    @Getter
    @Setter
    public static class PrototypeBean {
        private final long creationTime = System.currentTimeMillis();
        private int accessCount = 0;

        public String access() {
            accessCount++;
            return String.format("PrototypeBean [Created: %d, Accesses: %d, HashCode: %d]",
                creationTime, accessCount, this.hashCode());
        }
    }

    /**
     * Request-scoped bean class
     *
     * Note: Using @Getter/@Setter instead of @Data to preserve object identity hashCode.
     * Within a single HTTP request, the same instance is used, so hashCode should stay constant.
     * Different requests will get different instances (different hashCodes).
     */
    @Getter
    @Setter
    public static class RequestBean {
        private final long creationTime = System.currentTimeMillis();
        private int accessCount = 0;

        public String access() {
            accessCount++;
            return String.format("RequestBean [Created: %d, Accesses: %d, HashCode: %d]",
                creationTime, accessCount, this.hashCode());
        }
    }

    /**
     * Session-scoped bean class
     *
     * Note: Using @Getter/@Setter instead of @Data to preserve object identity hashCode.
     * Within the same HTTP session, the same instance is used, so hashCode should stay constant.
     * Different sessions will get different instances (different hashCodes).
     */
    @Getter
    @Setter
    public static class SessionBean {
        private final long creationTime = System.currentTimeMillis();
        private int accessCount = 0;
        private String sessionData = "Default session data";

        public String access() {
            accessCount++;
            return String.format("SessionBean [Created: %d, Accesses: %d, HashCode: %d, Data: %s]",
                creationTime, accessCount, this.hashCode(), sessionData);
        }
    }
}
