package com.poppin.poppinserver.alarm.dto.fcm.request;

import jakarta.validation.constraints.NotNull;

public record PushRequestDto(
        @NotNull
        String popupId,

        @NotNull
        String token
) {

}
