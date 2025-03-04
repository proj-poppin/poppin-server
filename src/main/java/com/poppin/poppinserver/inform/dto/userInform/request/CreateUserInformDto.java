package com.poppin.poppinserver.inform.dto.userInform.request;

import com.poppin.poppinserver.popup.dto.popup.request.CreateTasteDto;
import jakarta.validation.constraints.NotNull;

public record CreateUserInformDto(
        @NotNull
        String name,
        String contactLink
) {
}
