package com.poppin.poppinserver.alarm.dto.fcm.request;

import jakarta.validation.constraints.NotNull;

public record APNsRequestDto(

        @NotNull
        String token,

        @NotNull
        String title,

        @NotNull
        String body,

        @NotNull
        Boolean testType // dev : false, prod : true

) {
}
