package com.poppin.poppinserver.user.dto.faq.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;


@Builder
public record FaqRequestDto(
        @NotNull String question,
        @NotNull String answer
) {
}
