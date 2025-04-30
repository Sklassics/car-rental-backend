package com.sklassics.cars;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class CarsApplication {

	private static final Logger logger = LoggerFactory.getLogger(CarsApplication.class);

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		System.out.println("Default Timezone: " + java.util.TimeZone.getDefault().getID());

		SpringApplication.run(CarsApplication.class, args);
	}

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
		logger.info("Timezone set to Asia/Kolkata");
	}
}
