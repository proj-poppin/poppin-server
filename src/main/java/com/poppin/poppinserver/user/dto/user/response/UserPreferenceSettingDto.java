package com.poppin.poppinserver.user.dto.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record UserPreferenceSettingDto(
        boolean isPreferenceSettingCreated,
        @JsonProperty("userPreference") UserTasteResponseDto userTasteResponseDto
) {
    public static UserPreferenceSettingDto fromUserPreferenceInfo(
            boolean isPreferenceSettingCreated,
            UserTasteResponseDto userTasteResponseDto
    ) {
        return UserPreferenceSettingDto.builder()
                .isPreferenceSettingCreated(isPreferenceSettingCreated)
                .userTasteResponseDto(userTasteResponseDto)
                .build();
    }
}
