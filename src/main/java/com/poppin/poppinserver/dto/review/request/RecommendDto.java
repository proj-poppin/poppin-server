package com.poppin.poppinserver.dto.review.request;

public record RecommendDto(

        String fcmToken,

        Long reviewId,

        Long popupId
) {
}
