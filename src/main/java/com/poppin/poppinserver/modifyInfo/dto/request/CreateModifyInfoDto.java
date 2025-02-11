package com.poppin.poppinserver.modifyInfo.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateModifyInfoDto(
        @NotNull
        String popupId,
        @NotNull
        String content

) {
}
