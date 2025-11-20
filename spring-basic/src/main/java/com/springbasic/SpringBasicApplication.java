package com.springbasic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Basics Tutorial - Main Application
 *
 * This application demonstrates fundamental Spring Boot concepts:
 * 1. Singleton Pattern
 * 2. Spring Beans
 * 3. Bean Lifecycle
 * 4. Dependency Injection
 * 5. Annotations
 * 6. Configuration (application.properties)
 * 7. Environment Variables
 *
 * @author Spring Boot Learning Team
 * @version 1.0.0
 */
@SpringBootApplication
public class SpringBasicApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBasicApplication.class, args);

		System.out.println("\n" +
			"╔═══════════════════════════════════════════════════════╗\n" +
			"║   Spring Boot Basics Tutorial Started Successfully   ║\n" +
			"╠═══════════════════════════════════════════════════════╣\n" +
			"║                                                       ║\n" +
			"║  Application running at: http://localhost:9000        ║\n" +
			"║                                                       ║\n" +
			"║  Available Endpoints:                                 ║\n" +
			"║  • /api/singleton/*    - Singleton Pattern            ║\n" +
			"║  • /api/beans/*        - Spring Beans                 ║\n" +
			"║  • /api/lifecycle/*    - Bean Lifecycle               ║\n" +
			"║  • /api/di/*           - Dependency Injection         ║\n" +
			"║  • /api/annotations/*  - Annotations                  ║\n" +
			"║  • /api/config/*       - Configuration                ║\n" +
			"║  • /api/env/*          - Environment Variables        ║\n" +
			"║                                                       ║\n" +
			"║  📚 Check README.md for detailed documentation        ║\n" +
			"║                                                       ║\n" +
			"╚═══════════════════════════════════════════════════════╝\n"
		);
	}
}
