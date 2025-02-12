package com.poppin.poppinserver.user.dto.user.response;

import com.poppin.poppinserver.popup.dto.popup.response.PopupActivityResponseDto;
import com.poppin.poppinserver.review.dto.response.ReviewActivityResponseDto;
import lombok.Builder;

@Builder
public record UserActivityResponseDto(
        PopupActivityResponseDto popupActivities,
        ReviewActivityResponseDto reviewActivities,
        UserNotificationResponseDto notifications
) {
    public static UserActivityResponseDto fromProperties(PopupActivityResponseDto popupActivities,
                                                         ReviewActivityResponseDto reviewActivities,
                                                         UserNotificationResponseDto notifications) {
        return UserActivityResponseDto.builder()
                .popupActivities(popupActivities)
                .reviewActivities(reviewActivities)
                .notifications(notifications)
                .build();
    }
}
