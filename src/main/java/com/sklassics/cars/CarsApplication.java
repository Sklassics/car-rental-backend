package com.sklassics.cars;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import io.github.cdimascio.dotenv.Dotenv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EntityScan({ "com.sklassics.cars.entities", "com.sklassics.cars.admin.entities", "com.sklassics.cars.admin.controller",
		"com.sklassics.cars.admin.repositories", "com.sklassics.cars.admin.service", "com.sklassics.cars.controllers",
		"com.sklassics.cars.dtos", "com.sklassics.cars.exceptions", "com.sklassics.cars.repositories",
		"com.sklassics.cars.security", "com.sklassics.cars.services", "com.sklassics.cars.utility" })
@ComponentScan("com.sklassics.cars")
@EnableAsync

public class CarsApplication {

	private static final Logger logger = LoggerFactory.getLogger(CarsApplication.class);

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		// Optionally print the default timezone
		System.out.println("Default Timezone: " + TimeZone.getDefault().getID());

		// Start the Spring Boot application
		SpringApplication.run(CarsApplication.class, args);
	}

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
		logger.info("Timezone set to Asia/Kolkata");
	}
}
