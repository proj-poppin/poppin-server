package com.poppin.poppinserver;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

// @SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableScheduling
@SpringBootApplication
public class PoppinServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PoppinServerApplication.class, args);
	}
}
