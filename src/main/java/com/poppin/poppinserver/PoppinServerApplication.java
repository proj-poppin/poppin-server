package com.poppin.poppinserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// @SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableScheduling
@SpringBootApplication
public class PoppinServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PoppinServerApplication.class, args);
    }
}
