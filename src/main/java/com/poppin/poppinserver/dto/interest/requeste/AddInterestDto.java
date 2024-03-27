package com.poppin.poppinserver.dto.interest.requeste;

import jakarta.validation.constraints.NotNull;

public record AddInterestDto(
        @NotNull
        Long popupId
) {
}
