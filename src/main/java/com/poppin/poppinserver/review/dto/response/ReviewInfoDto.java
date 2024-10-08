package com.poppin.poppinserver.review.dto.response;

import com.poppin.poppinserver.review.domain.Review;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record ReviewInfoDto(

        @NotNull
        String reviewId,

        @NotNull
        String nickname,

        @NotNull
        Integer reviewCnt, // 사용자가 쓴 리뷰 개수

        @NotNull
        String text,

        @NotNull
        List<String> imageUrls, // 후기 첨부 이미지

        @NotNull
        String profileUrl,

        @NotNull
        boolean isCertificated,

        @NotNull
        int recommendCnt,

        @NotNull
        String userId
) {
    public static List<ReviewInfoDto> fromEntityList(List<Review> reviews, List<List<String>> imageUrls,
                                                     List<String> profileUrls, List<Integer> reviewCnt) {
        List<ReviewInfoDto> reviewInfoDtoList = new ArrayList<>();

        for (int i = 0; i < reviews.size(); i++) {
            ReviewInfoDto reviewInfoDto = ReviewInfoDto.builder()
                    .reviewId(String.valueOf(reviews.get(i).getId()))
                    .userId(String.valueOf(reviews.get(i).getUser().getId()))
                    .nickname(reviews.get(i).getNickname())
                    .reviewCnt(reviewCnt.get(i))
                    .text(reviews.get(i).getText())
                    .imageUrls(imageUrls.get(i))
                    .profileUrl(profileUrls.get(i))
                    .isCertificated(reviews.get(i).getIsCertificated())
                    .recommendCnt(reviews.get(i).getRecommendCnt())
                    .build();

            reviewInfoDtoList.add(reviewInfoDto);
        }
        return reviewInfoDtoList;
    }
}
