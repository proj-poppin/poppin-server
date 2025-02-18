package com.poppin.poppinserver.review.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.review.controller.swagger.SwaggerReviewQueryController;
import com.poppin.poppinserver.review.dto.response.ReviewDto;
import com.poppin.poppinserver.review.dto.response.ReviewListDto;
import com.poppin.poppinserver.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewQueryController implements SwaggerReviewQueryController {

    private final ReviewService reviewService;

    @GetMapping( "/list")
    public ResponseDto<List<ReviewListDto>> readReviewList(@UserId Long userId) {
        return ResponseDto.ok(reviewService.readReviewList(userId));
    }

    @GetMapping("/read")
    public ResponseDto<ReviewDto> readReview(@UserId Long userId, @RequestParam(value = "reviewId") String reviewId){
        return ResponseDto.ok(reviewService.readReview(userId, reviewId));
    }
}
