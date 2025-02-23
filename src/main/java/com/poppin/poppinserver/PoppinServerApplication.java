package com.poppin.poppinserver;

import com.poppin.poppinserver.core.config.AwsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

// @SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableScheduling
@EnableConfigurationProperties(AwsProperties.class)
@SpringBootApplication
public class PoppinServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PoppinServerApplication.class, args);
    }
}
