package com.poppin.poppinserver.dto.user.response;

import lombok.Builder;

@Builder
public record UserPreferenceSettingDto(
        boolean isPreferenceSettingCreated
) {
}
