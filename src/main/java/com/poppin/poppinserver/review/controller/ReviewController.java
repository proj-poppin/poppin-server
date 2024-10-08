package com.poppin.poppinserver.review.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.review.dto.request.CreateReviewDto;
import com.poppin.poppinserver.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    // TODO: 삭제 예정
    @PostMapping(value = "/test/image", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE}) // 후기 생성
    public ResponseDto<?> imageTest(
            @RequestPart(value = "images") List<MultipartFile> images) {
        if (!images.isEmpty()) {
            return ResponseDto.ok("이미지 전달 성공");
        } else {
            return ResponseDto.ok("이미지 전달 실패");
        }
    }

    // TODO: 삭제 예정
    @PostMapping(value = "/test/contents", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> contentTest(@RequestPart(value = "contents") CreateReviewDto createReviewDto) {
        return ResponseDto.ok("콘텐츠 전달 완료");
    }

    /*후기 작성하기*/
    @PostMapping(value = "/write", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> writeReview(
            @UserId Long userId,
            @RequestParam("popupId") Long popupId,
            @RequestParam("text") String text,
            @RequestParam("visitDate") String visitDate,
            @RequestParam("satisfaction") String satisfaction,
            @RequestParam("congestion") String congestion,
            @RequestPart(value = "images") List<MultipartFile> images) {

        return ResponseDto.ok(reviewService.writeReview(userId, popupId, text, visitDate, satisfaction, congestion, images));
    }

    @PostMapping("/add-recommend") // 후기 추천
    public ResponseDto<?> recommendReview(@UserId Long userId,
                                          @RequestParam("reviewId") String reviewId,
                                          @RequestParam("popupId") String popupId
    ) {
        return ResponseDto.ok(reviewService.recommendReview(userId, reviewId, popupId));
    }

}
