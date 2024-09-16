package com.poppin.poppinserver.user.dto.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.dto.auth.response.JwtTokenDto;
import lombok.Builder;

@Builder
public record UserInfoResponseDto(
        @JsonProperty("userNotificationSetting") UserNotificationSettingResponseDto userNotificationSettingResponseDto,
        @JsonProperty("user") UserSchemaResponseDto userSchemaResponseDto,
        @JsonProperty("jwtToken") JwtTokenDto jwtTokenDto,
        @JsonProperty("userPreferenceSetting") UserPreferenceSettingDto userPreferenceSettingDto
) {
    public static UserInfoResponseDto fromUserEntity(
            User user,
            AlarmSetting alarmSetting,
            JwtTokenDto jwtTokenDto,
            UserPreferenceSettingDto userPreferenceSettingDto
    ) {
        return UserInfoResponseDto.builder()
                .userNotificationSettingResponseDto(UserNotificationSettingResponseDto.fromEntity(alarmSetting))
                .jwtTokenDto(jwtTokenDto)
                .userSchemaResponseDto(UserSchemaResponseDto.fromUserEntity(user))
                .userPreferenceSettingDto(
                        UserPreferenceSettingDto.fromUserPreferenceInfo(
                                userPreferenceSettingDto.isPreferenceSettingCreated(),
                                userPreferenceSettingDto.userTasteDto())
                )
                .build();
    }
}
