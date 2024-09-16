package com.poppin.poppinserver.alarm.dto.alarmSetting.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AlarmSettingRequestDto(
        @NotNull String fcmToken,
        @NotNull Boolean appPush,
        @NotNull Boolean nightPush,
        @NotNull Boolean helpfulReviewPush,
        @NotNull Boolean interestedPopupOpenPush,
        @NotNull Boolean interestedPopupDeadlinePush,
        @NotNull Boolean interestedPopupInfoUpdatedPush
) {
}
