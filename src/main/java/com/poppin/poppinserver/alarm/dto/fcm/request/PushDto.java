package com.poppin.poppinserver.alarm.dto.fcm.request;

import jakarta.validation.constraints.NotNull;


public record PushDto(
        @NotNull
        String token,

        @NotNull
        String title,

        @NotNull
        String body,

        @NotNull
        String popupId

) {
}
