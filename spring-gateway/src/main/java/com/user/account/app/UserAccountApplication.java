package com.user.account.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class UserAccountApplication {

	private static final Logger logger = LoggerFactory.getLogger(UserAccountApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(UserAccountApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		logger.info("=".repeat(80));
		logger.info("Spring Cloud Gateway is up and running!");
		logger.info("=".repeat(80));
	}

}
