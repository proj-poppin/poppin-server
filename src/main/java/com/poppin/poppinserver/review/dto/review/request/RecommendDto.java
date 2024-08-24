package com.poppin.poppinserver.review.dto.review.request;

public record RecommendDto(

        String fcmToken,

        Long reviewId,

        Long popupId
) {
}
