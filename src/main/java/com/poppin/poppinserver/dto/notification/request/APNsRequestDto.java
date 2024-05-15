package com.poppin.poppinserver.dto.notification.request;

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
