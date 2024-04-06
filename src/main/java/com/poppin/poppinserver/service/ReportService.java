package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.Review;
import com.poppin.poppinserver.dto.review.request.ReviewInfoDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReviewRepository reviewRepository;

    public Review hideReview(Long userId, ReviewInfoDto reviewInfoDto){


        /*관리자 여부 체크 메서드도 필요*/
        /**/

        Review review = reviewRepository.findById(reviewInfoDto.reviewId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_REVIEW));

        review.updateReviewVisible();

        return reviewRepository.save(review);

    }
}
