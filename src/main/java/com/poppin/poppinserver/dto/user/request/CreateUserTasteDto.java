package com.poppin.poppinserver.dto.user.request;

import com.poppin.poppinserver.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.dto.popup.request.CreateWhoWithDto;
import jakarta.validation.constraints.NotNull;

public record CreateUserTasteDto(
        @NotNull CreatePreferedDto preference,
        @NotNull CreateTasteDto taste,
        @NotNull CreateWhoWithDto whoWith
) {

}
