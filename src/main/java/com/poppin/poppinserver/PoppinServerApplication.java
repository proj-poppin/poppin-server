package com.poppin.poppinserver;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// @SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableBatchProcessing
@SpringBootApplication
public class PoppinServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PoppinServerApplication.class, args);
	}

}
