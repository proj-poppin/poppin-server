package com.poppin.poppinserver.dto.userInform.request;

import com.poppin.poppinserver.dto.popup.request.CreateTasteDto;
import jakarta.validation.constraints.NotNull;

public record CreateUserInform(
        @NotNull
        String name,
        @NotNull
        CreateTasteDto taste,
        String contactlink
) {
}
