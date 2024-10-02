package com.poppin.poppinserver.user.dto.user.response;

import com.poppin.poppinserver.alarm.dto.NotificationResponseDto;
import java.util.List;
import lombok.Builder;

@Builder
public record UserNotificationResponseDto(
        List<NotificationResponseDto> popups,
        List<NotificationResponseDto> notices
) {
    public static UserNotificationResponseDto fromDtoList(List<NotificationResponseDto> popups,
                                                          List<NotificationResponseDto> notices) {
        return UserNotificationResponseDto.builder()
                .popups(popups)
                .notices(notices)
                .build();
    }
}
