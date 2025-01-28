package com.poppin.poppinserver.core.dto;

import com.poppin.poppinserver.core.constant.Constants;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record AppInfoResponseDto(
        AppVersionResponseDto APP_VERSION_INFO,
        String APPLE_APP_STORE_URL,
        String SERVICE_TERMS,
        String PRIVACY_TERMS
) {
    public static AppInfoResponseDto fromConstants() {
        return AppInfoResponseDto.builder()
                .APP_VERSION_INFO(AppVersionResponseDto.fromConstants())
                .APPLE_APP_STORE_URL(Constants.APPLE_APP_STORE_URL)
                .SERVICE_TERMS(Constants.SERVICE_TERMS)
                .PRIVACY_TERMS(Constants.PRIVACY_TERMS)
                .build();
    }
}
