package com.poppin.poppinserver.user.dto.user.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record UserReviewDto(
        Long reviewId,
        String popupName,
        String visitedAt,
        String createdAt,
        String content,
        List<String> imageUrl, /* 후기 사진 리스트 */
        Boolean visible
) {
    public static UserReviewDto of(Long reviewId, String popupName, String visitedAt, String createdAt, String content,
                                   List<String> imageUrl, Boolean visible) {
        return UserReviewDto.builder()
                .reviewId(reviewId)
                .popupName(popupName)
                .visitedAt(visitedAt)
                .createdAt(createdAt)
                .content(content)
                .imageUrl(imageUrl)
                .visible(visible)
                .build();
    }
}
