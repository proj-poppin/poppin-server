package com.poppin.poppinserver.alarm.dto.alarm.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AlarmSettingRequestDto(
        @NotNull Boolean appPush,
        @NotNull Boolean nightPush,
        @NotNull Boolean helpfulReviewPush,
        @NotNull Boolean interestedPopupOpenPush,
        @NotNull Boolean interestedPopupDeadlinePush,
        @NotNull Boolean interestedPopupInfoUpdatedPush
) {
}
