package com.poppin.poppinserver.dto.fcm.request;

import jakarta.validation.constraints.NotNull;

public record PushRequestDto(
        @NotNull
        Long popupId,

        @NotNull
        String token
){

}
