package com.poppin.poppinserver.dto.review.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.poppin.poppinserver.domain.Review;
import com.poppin.poppinserver.domain.VisitorData;
import lombok.Builder;

import java.time.LocalDateTime;


@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewDto(
        Long id,
        String text,
        String congestion,
        String satisfaction,
        String visitDate,
        LocalDateTime createdAt,
        Boolean isCertificated,
        Integer recommendCnt
) {
    public static ReviewDto fromEntity(Review review,VisitorData visitorData){
        VisitorData vd = visitorData;
        return ReviewDto.builder()
                .id(review.getId())
                .text(review.getText())
                .congestion(vd.getCongestion())
                .satisfaction(vd.getSatisfaction())
                .visitDate(vd.getVisitDate())
                .createdAt(review.getCreatedAt())
                .isCertificated(review.getIsCertificated())
                .recommendCnt(review.getRecommendCnt())
                .build();
    }
}
