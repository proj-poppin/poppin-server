package com.poppin.poppinserver.dto.popup.request;

import jakarta.validation.constraints.NotNull;

public record CreateUserTasteDto(
        @NotNull CreatePreferedDto prefered,
        @NotNull CreateTasteDto taste,
        @NotNull CreateWhoWithDto whoWith
) {

}
