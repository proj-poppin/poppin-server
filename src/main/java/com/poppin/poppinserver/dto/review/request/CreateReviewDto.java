package com.poppin.poppinserver.dto.review.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CreateReviewDto (

    @NotNull
    Long popupId,

    @NotNull
    String text,

    @NotNull
    String visitDate,

    @NotNull
    String satisfaction,

    @NotNull
    String congestion,

    @NotNull
    Boolean isCertificated,

    @NotNull
    int recommendCnt
    ){

}
