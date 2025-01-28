package com.poppin.poppinserver.user.dto.user.response;

import com.poppin.poppinserver.popup.dto.popup.response.PopupActivityResponseDto;
import lombok.Builder;

@Builder
public record UserActivityResponseDto(
        PopupActivityResponseDto popupActivities,
        UserNotificationResponseDto notifications
) {
    public static UserActivityResponseDto fromProperties(PopupActivityResponseDto popupActivities,
                                                         UserNotificationResponseDto notifications) {
        return UserActivityResponseDto.builder()
                .popupActivities(popupActivities)
                .notifications(notifications)
                .build();
    }
}
