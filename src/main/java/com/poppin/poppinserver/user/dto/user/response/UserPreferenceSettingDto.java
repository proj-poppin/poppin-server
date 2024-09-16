package com.poppin.poppinserver.user.dto.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poppin.poppinserver.popup.dto.popup.response.UserTasteDto;
import lombok.Builder;

@Builder
public record UserPreferenceSettingDto(
        boolean isPreferenceSettingCreated,
        @JsonProperty("userPreference") UserTasteDto userTasteDto
) {
    public static UserPreferenceSettingDto fromUserPreferenceInfo(
            boolean isPreferenceSettingCreated,
            UserTasteDto userTasteDto
    ) {
        return UserPreferenceSettingDto.builder()
                .isPreferenceSettingCreated(isPreferenceSettingCreated)
                .userTasteDto(userTasteDto)
                .build();
    }
}
