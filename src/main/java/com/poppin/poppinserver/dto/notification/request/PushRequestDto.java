package com.poppin.poppinserver.dto.notification.request;

import jakarta.validation.constraints.NotNull;

public record PushRequestDto(
        @NotNull
        Long popupId,

        @NotNull
        String token
){

}
