package com.poppin.poppinserver.core.dto;

import com.poppin.poppinserver.core.constant.Constants;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record AppVersionResponseDto(
        String recentVersion,
        String requiredVersion,
        String requiredIOSVersion,
        String requiredAndroidVersion
) {
    public static AppVersionResponseDto fromConstants() {
        return AppVersionResponseDto.builder()
                .recentVersion(Constants.RECENT_VERSION)
                .requiredVersion(Constants.REQUIRED_VERSION)
                .requiredIOSVersion(Constants.REQUIRED_IOS_VERSION)
                .requiredAndroidVersion(Constants.REQUIRED_ANDROID_VERSION)
                .build();
    }
}
