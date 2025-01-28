package com.poppin.poppinserver.review.usecase;

import com.poppin.poppinserver.review.domain.ReviewImage;

import java.util.List;

public interface ReviewImageQueryUseCase {
    List<String> findUrlAllByReviewId(Long reviewId);

    List<ReviewImage> findAllByReviewId(Long reviewId);
}
