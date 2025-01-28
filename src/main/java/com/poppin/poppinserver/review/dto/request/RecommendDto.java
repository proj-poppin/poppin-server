package com.poppin.poppinserver.review.dto.request;

public record RecommendDto(

        String fcmToken,

        String reviewId,

        String popupId
) {
}
