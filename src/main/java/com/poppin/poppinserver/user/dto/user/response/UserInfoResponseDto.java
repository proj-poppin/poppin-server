package com.poppin.poppinserver.user.dto.user.response;

import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.dto.auth.response.JwtTokenDto;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record UserInfoResponseDto(
        UserNotificationSettingResponseDto userNotificationSetting,
        UserSchemaResponseDto user,
        JwtTokenDto jwtToken,
        UserPreferenceSettingDto userPreferenceSetting,
        UserNoticeResponseDto userNotice,
        UserActivityResponseDto userActivities,
        UserRelationDto userRelation,
        boolean isPreferenceSettingCreated
) {
    public static UserInfoResponseDto fromUserEntity(
            User user,
            AlarmSetting alarmSetting,
            JwtTokenDto jwtTokenDto,
            UserPreferenceSettingDto userPreferenceSettingDto,
            UserNoticeResponseDto userNoticeResponseDto,
            UserActivityResponseDto userActivities,
            UserRelationDto userRelation,
            boolean isPreferenceSettingCreated
    ) {
        return UserInfoResponseDto.builder()
                .userNotificationSetting(UserNotificationSettingResponseDto.fromEntity(alarmSetting))
                .jwtToken(jwtTokenDto)
                .user(UserSchemaResponseDto.fromUserEntity(user))
                .userPreferenceSetting(userPreferenceSettingDto)
                .isPreferenceSettingCreated(isPreferenceSettingCreated)
                .userNotice(userNoticeResponseDto)
                .userActivities(userActivities)
                .userRelation(userRelation)
                .build();
    }
}
