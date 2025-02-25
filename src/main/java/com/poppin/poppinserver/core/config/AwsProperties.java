package com.poppin.poppinserver.core.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "secrets")
public class AwsProperties {
    private Credentials credentials;
    private Region region;

    @Getter
    @Setter
    public static class Credentials {
        private String AWS_ACCESS_KEY_ID;
        private String AWS_SECRET_ACCESS_KEY;
    }

    @Getter
    @Setter
    public static class Region {
        private String AWS_REGION;
    }

    @PostConstruct
    public void validateProperties() {
        if (credentials == null || credentials.AWS_ACCESS_KEY_ID == null || credentials.AWS_SECRET_ACCESS_KEY == null) {
            throw new IllegalStateException("❌ AWS Credentials (Access Key / Secret Key) 가 설정되지 않았습니다!");
        }
        if (region == null || region.AWS_REGION == null) {
            throw new IllegalStateException("❌ AWS Region이 설정되지 않았습니다!");
        }

        System.out.println("✅ AWS Credentials 로드 성공!");
        System.out.println("🔑 AWS Access Key: " + credentials.AWS_ACCESS_KEY_ID);
        System.out.println("🌍 AWS Region: " + region.AWS_REGION);
    }
}
