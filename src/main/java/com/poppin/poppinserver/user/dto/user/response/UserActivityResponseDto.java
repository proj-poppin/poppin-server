package com.poppin.poppinserver.user.dto.user.response;

import com.poppin.poppinserver.popup.dto.popup.response.PopupScrapDto;
import java.util.List;
import lombok.Builder;

@Builder
public record UserActivityResponseDto(
        List<PopupScrapDto> scrappedPopups,
        UserNotificationResponseDto notifications
) {
    public static UserActivityResponseDto fromProperties(List<PopupScrapDto> scrappedPopups,
                                                         UserNotificationResponseDto notifications) {
        return UserActivityResponseDto.builder()
                .scrappedPopups(scrappedPopups)
                .notifications(notifications)
                .build();
    }
}
