package com.poppin.poppinserver.popup.dto.popup.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record VisitorsInfoDto(
        @NotNull
        Long popupId,

        @NotNull
        String fcmToken //fcm
) {

}
