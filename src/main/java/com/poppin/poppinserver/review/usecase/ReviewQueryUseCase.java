package com.poppin.poppinserver.review.usecase;

import com.poppin.poppinserver.review.domain.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewQueryUseCase {

    Optional<Review> findById(Long reviewId);

    List<Review> findByUserId(Long userId);



}
