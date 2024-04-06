package com.poppin.poppinserver.dto.modifyInfo.request;

import jakarta.validation.constraints.NotNull;

public record CreateModifyInfoDto(
        @NotNull
        Long popupId,
        @NotNull
        String content

) {
}
