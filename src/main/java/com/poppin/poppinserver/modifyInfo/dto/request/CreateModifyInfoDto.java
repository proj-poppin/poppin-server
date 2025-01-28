package com.poppin.poppinserver.modifyInfo.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateModifyInfoDto(
        @NotNull
        Long popupId,
        @NotNull
        String content

) {
}
