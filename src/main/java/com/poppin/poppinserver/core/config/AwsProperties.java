package com.poppin.poppinserver.core.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cloud.aws")
public class AwsProperties {
    private Credentials credentials;
    private Region region;

    @Getter
    @Setter
    public static class Credentials {
        private String accessKey;
        private String secretKey;
    }

    @Getter
    @Setter
    public static class Region {
        private String staticRegion;
    }

    @PostConstruct
    public void validateProperties() {
        if (credentials == null || credentials.accessKey == null || credentials.secretKey == null) {
            throw new IllegalStateException("❌ AWS Credentials (Access Key / Secret Key) 가 설정되지 않았습니다!");
        }
        if (region == null || region.staticRegion == null) {
            throw new IllegalStateException("❌ AWS Region이 설정되지 않았습니다!");
        }

        System.out.println("✅ AWS Credentials 로드 성공!");
        System.out.println("🔑 AWS Access Key: " + credentials.accessKey);
        System.out.println("🌍 AWS Region: " + region.staticRegion);
    }
}
