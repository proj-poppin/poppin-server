package com.poppin.poppinserver.review.dto.request;

public record RecommendDto(

        String fcmToken,

        Long reviewId,

        Long popupId
) {
}
