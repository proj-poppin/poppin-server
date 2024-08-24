package com.poppin.poppinserver.review.dto.review.request;

import jakarta.validation.constraints.NotNull;

public record CreateReviewDto (

    @NotNull
    Long popupId,
    @NotNull
    String nickname,
    @NotNull
    String text,
    @NotNull
    String visitDate, // 평일 오전, 오후 / 주말 오전, 오후
    @NotNull
    String satisfaction, // 만족 / 보통 / 불만족
    @NotNull
    String congestion // 혼잡 / 보통 / 여유

    ){

}
