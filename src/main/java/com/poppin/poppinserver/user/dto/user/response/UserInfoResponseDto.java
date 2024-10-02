package com.poppin.poppinserver.user.dto.user.response;

import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.dto.auth.response.JwtTokenDto;
import lombok.Builder;

@Builder
public record UserInfoResponseDto(
        UserNotificationSettingResponseDto userNotificationSetting,
        UserSchemaResponseDto user,
        JwtTokenDto jwtToken,
        UserPreferenceSettingDto userPreferenceSetting
) {
    public static UserInfoResponseDto fromUserEntity(
            User user,
            AlarmSetting alarmSetting,
            JwtTokenDto jwtTokenDto,
            UserPreferenceSettingDto userPreferenceSettingDto
    ) {
        return UserInfoResponseDto.builder()
                .userNotificationSetting(UserNotificationSettingResponseDto.fromEntity(alarmSetting))
                .jwtToken(jwtTokenDto)
                .user(UserSchemaResponseDto.fromUserEntity(user))
                .userPreferenceSetting(
                        UserPreferenceSettingDto.fromUserPreferenceInfo(
                                userPreferenceSettingDto.isPreferenceSettingCreated(),
                                userPreferenceSettingDto.userTasteResponseDto())
                )
                .build();
    }
}
