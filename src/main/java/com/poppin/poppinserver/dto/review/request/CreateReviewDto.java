package com.poppin.poppinserver.dto.review.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;


@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
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
    String congestion, // 혼잡 / 보통 / 여유
    @NotNull
    boolean isCertificated,
    @NotNull
    int recommendCnt
    ){

}
