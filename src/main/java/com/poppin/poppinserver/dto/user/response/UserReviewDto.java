package com.poppin.poppinserver.dto.user.response;

import lombok.Builder;

import java.util.List;

@Builder
public record UserReviewDto(
        Long reviewId,
        String popupName,
        String visitedAt,
        String createdAt,
        String content,
        List<String> imageUrl, /* 후기 사진 리스트 */
        Boolean hiddenReview
) {
}
