package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.review.request.CreateReviewDto;
import com.poppin.poppinserver.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping(value = "/create", consumes = {MediaType.APPLICATION_JSON_VALUE , MediaType.MULTIPART_FORM_DATA_VALUE}) // 후기 생성
    public ResponseDto<?> createReview(@RequestPart(value = "contents") @Valid CreateReviewDto createReviewDto , @RequestPart(value = "images") List<MultipartFile> images){
        return ResponseDto.ok(reviewService.createReview(createReviewDto, images));
    }

    @PostMapping("/add-recommend") // 후기 추천
    public ResponseDto<?> addRecommendReview(@UserId Long userId, @RequestParam(value = "reviewId") Long reviewId, @RequestParam(value = "popupId")Long popupId){
        return ResponseDto.ok(reviewService.addRecommendReview(userId, reviewId, popupId));
    }

}
