package com.poppin.poppinserver.user.dto.user.response;

import lombok.Builder;

@Builder
public record UserPreferenceSettingDto(
        boolean isPreferenceSettingCreated
) {
}
