package com.poppin.poppinserver.config;

import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class APNsConfig {

    @Bean
    public ApnsConfig apnsConfig() {
        return ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setSound("default")
                        .setBadge(1)
                        .build())
                .build();
    }

    public ApnsConfig createApnsConfig(String title, String body) {
        return ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setAlert(ApsAlert.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .setSound("default")
                        .setCategory("push_click")
                        .setBadge(1)
                        .setContentAvailable(true)

                        .build())
                .build();
    }
}
