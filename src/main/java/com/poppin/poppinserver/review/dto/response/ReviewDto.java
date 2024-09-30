package com.poppin.poppinserver.review.dto.response;

import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.visit.domain.VisitorData;
import lombok.Builder;

import java.time.LocalDateTime;


@Builder
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
    public static ReviewDto fromEntity(Review review, VisitorData visitorData) {
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
