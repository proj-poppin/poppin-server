package com.poppin.poppinserver.review.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ReviewActivityResponseDto(
        List<Long> recommendReviews
) {
    public static ReviewActivityResponseDto fromProperties(List<Long> recommendReviews){
        return ReviewActivityResponseDto.builder()
                .recommendReviews(recommendReviews)
                .build();
    }
}
