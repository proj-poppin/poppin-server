package com.poppin.poppinserver.review.dto.review.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ReviewInfoDto(
        @NotNull
        Long reviewId
) {
}
