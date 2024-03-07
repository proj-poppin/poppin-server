package com.poppin.poppinserver.dto.review.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.poppin.poppinserver.domain.Review;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewDto(

        Long id,
        String text,
        String visitDate,
        String satisfaction,
        String congestion,
        LocalDateTime createdAt,
        Boolean isCertificated,
        int recommendCnt
) {
    public static ReviewDto fromEntity(Review review){
        return ReviewDto.builder()
                .id(review.getId())
                .text(review.getText())
                .visitDate(review.getVisitDate())
                .satisfaction(review.getSatisfaction())
                .congestion(review.getCongestion())
                .createdAt(review.getCreatedAt())
                .isCertificated(review.getIsCertificated())
                .recommendCnt(review.getRecommendCnt())
                .build();
    }
}
