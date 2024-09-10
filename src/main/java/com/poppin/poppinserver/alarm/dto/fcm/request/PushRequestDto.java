package com.poppin.poppinserver.alarm.dto.fcm.request;

import jakarta.validation.constraints.NotNull;

public record PushRequestDto(
        @NotNull
        Long popupId,

        @NotNull
        String token
) {

}
