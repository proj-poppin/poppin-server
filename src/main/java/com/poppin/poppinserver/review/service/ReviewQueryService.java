package com.poppin.poppinserver.review.service;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.review.repository.ReviewQueryRepository;
import com.poppin.poppinserver.review.usecase.ReviewQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryService implements ReviewQueryUseCase {

    private final ReviewQueryRepository reviewQueryRepository;

    @Override
    public Optional<Review> findById(Long reviewId) {
        return Optional.ofNullable(reviewQueryRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_REVIEW)));
    }

    @Override
    public List<Review> findByUserId(Long userId) {
        return reviewQueryRepository.findByUserId(userId);
    }
}
