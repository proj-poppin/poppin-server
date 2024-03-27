package com.poppin.poppinserver.dto.review.response;

import com.poppin.poppinserver.domain.Review;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record ReviewInfoDto(

        @NotNull
        Long reviewId,

        @NotNull
        String nickname,

        @NotNull
        int totalReviewWrite, // 사용자가 쓴 리뷰 개수

        @NotNull
        String text,

        @NotNull
        String imageUrl,

        @NotNull
        boolean isCertificated,

        @NotNull
        int recommendCnt
) {
    public static List<ReviewInfoDto> fromEntityList(List<Review> reviews, int totalReviewWrite){
        List<ReviewInfoDto> reviewInfoDtoList = new ArrayList<>();

        for (Review review: reviews
             ) {
            ReviewInfoDto reviewInfoDto = ReviewInfoDto.builder()
                    .reviewId(review.getId())
                    .nickname(review.getNickname())
                    .totalReviewWrite(totalReviewWrite)
                    .text(review.getText())
                    .imageUrl(review.getImageUrl())
                    .isCertificated(review.getIsCertificated())
                    .recommendCnt(review.getRecommendCnt())
                    .build();

            reviewInfoDtoList.add(reviewInfoDto);
        }
        return reviewInfoDtoList;
    }
}
