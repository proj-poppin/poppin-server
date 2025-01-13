package com.poppin.poppinserver.review.service;

import com.poppin.poppinserver.review.domain.ReviewImage;
import com.poppin.poppinserver.review.repository.ReviewImageQueryRepository;
import com.poppin.poppinserver.review.usecase.ReviewImageQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewImageQueryService implements ReviewImageQueryUseCase {

    private final ReviewImageQueryRepository reviewImageQueryRepository;

    @Override
    public List<String> findUrlAllByReviewId(Long reviewId) {
        return reviewImageQueryRepository.findUrlAllByReviewId(reviewId);
    }

    @Override
    public List<ReviewImage> findAllByReviewId(Long reviewId) {
        return reviewImageQueryRepository.findAllByReviewId(reviewId);
    }
}
