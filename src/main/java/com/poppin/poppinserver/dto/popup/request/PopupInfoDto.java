package com.poppin.poppinserver.dto.popup.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PopupInfoDto(
        @NotNull
       Long popupId,

        @NotNull
        String token //fcm
) {

}
