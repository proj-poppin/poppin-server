package com.poppin.poppinserver.user.dto.auth.request;

import lombok.Builder;

@Builder
public record AppStartRequestDto(
        String os,
        String fcmToken,
        String appVersion
) {
    public static AppStartRequestDto of(String os, String fcmToken, String appVersion) {
        return AppStartRequestDto.builder()
                .os(os)
                .fcmToken(fcmToken)
                .appVersion(appVersion)
                .build();
    }
}
