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
        Long reviewCnt, // 사용자가 쓴 리뷰 개수

        @NotNull
        String text,

        @NotNull
        List<String> imageUrls,

        @NotNull
        boolean isCertificated,

        @NotNull
        int recommendCnt
) {
    public static List<ReviewInfoDto> fromEntityList(List<Review> reviews, List<List<String>> imageUrls, List<Long> reviewCnt){
        List<ReviewInfoDto> reviewInfoDtoList = new ArrayList<>();

        for (int i = 0; i < reviews.size(); i++) {
            ReviewInfoDto reviewInfoDto = ReviewInfoDto.builder()
                    .reviewId(reviews.get(i).getId())
                    .nickname(reviews.get(i).getNickname())
                    .reviewCnt(reviewCnt.get(i))
                    .text(reviews.get(i).getText())
                    .imageUrls(imageUrls.get(i))
                    .isCertificated(reviews.get(i).getIsCertificated())
                    .recommendCnt(reviews.get(i).getRecommendCnt())
                    .build();

            reviewInfoDtoList.add(reviewInfoDto);
        }
        return reviewInfoDtoList;
    }
}
