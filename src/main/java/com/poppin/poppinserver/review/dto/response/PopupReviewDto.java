package com.poppin.poppinserver.review.dto.response;

import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.review.domain.ReviewImage;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record PopupReviewDto(
        String profileUrl,
        String reviewId,
        String nickname,
        String userId,
        String text,
        List<String> imageUrls,
        boolean isCertificated,
        int recommendCnt
) {
    public static PopupReviewDto fromEntity(Review review) {
        List<String> imageUrls = review.getReviewImages()
                .stream()
                .map(ReviewImage::getImageUrl)
                .toList();
        return PopupReviewDto.builder()
                .profileUrl(review.getImageUrl())
                .reviewId(String.valueOf(review.getId()))
                .nickname(review.getNickname())
                .userId(String.valueOf(review.getUser().getId()))
                .text(review.getText())
                .imageUrls(imageUrls)
                .isCertificated(review.getIsCertificated())
                .recommendCnt(review.getRecommendCnt())
                .build();
    }

    public static List<PopupReviewDto> fromEntities(List<Review> reviews) {
        List<PopupReviewDto> dtos = new ArrayList<>();
        for (Review review : reviews) {
            dtos.add(PopupReviewDto.fromEntity(review));
        }
        return dtos;
    }
}