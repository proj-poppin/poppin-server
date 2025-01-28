package com.poppin.poppinserver.core.config;

import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import org.springframework.context.annotation.Configuration;

@Configuration
public class APNsConfiguration {

    public ApnsConfig apnsConfig(int badge) {
        return ApnsConfig.builder()
                .putHeader("apns-priority", "10")
                .setAps(Aps.builder()
                        .setBadge(badge)
                        .setSound("default")
                        .build())
                .build();
    }
}

