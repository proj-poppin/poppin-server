package com.poppin.poppinserver.admin.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;


@Builder
public record AdminFaqRequestDto(
        @NotNull String question,
        @NotNull String answer
) {
}
