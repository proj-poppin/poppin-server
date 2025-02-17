package com.poppin.poppinserver.review.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ReviewActivityResponseDto(
        List<String> recommendReviews
) {
    public static ReviewActivityResponseDto fromProperties(List<String> recommendReviews){
        return ReviewActivityResponseDto.builder()
                .recommendReviews(recommendReviews)
                .build();
    }
}
