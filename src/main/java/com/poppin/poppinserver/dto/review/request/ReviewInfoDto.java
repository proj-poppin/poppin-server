package com.poppin.poppinserver.dto.review.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ReviewInfoDto(
        @NotNull
        Long reviewId
) {
}
