package com.poppin.poppinserver.config;

import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class APNsConfiguration {

    @Bean
    public ApnsConfig apnsConfig() {
        return ApnsConfig.builder()
                .putHeader("apns-priority", "10")
                .setAps(Aps.builder()
                        .setBadge(1)
                        .setSound("default")
                        .build())
                .build();
    }
}
