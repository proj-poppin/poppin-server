package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.review.request.CreateReviewDto;
import com.poppin.poppinserver.service.ReviewService;
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

    /*
        이미지 테스트용
     */
    @PostMapping(value = "/test/image", consumes = {MediaType.APPLICATION_JSON_VALUE , MediaType.MULTIPART_FORM_DATA_VALUE}) // 후기 생성
    public ResponseDto<?> imageTest(
            @RequestPart(value = "images" ) List<MultipartFile> images)
    {
        if (!images.isEmpty()){return ResponseDto.ok("이미지 전달 성공");}
        else return ResponseDto.ok("이미지 전달 실패");
    }

    /*
        콘텐츠 테스트용
     */
    @PostMapping(value = "/test/contents", consumes = {MediaType.APPLICATION_JSON_VALUE , MediaType.MULTIPART_FORM_DATA_VALUE}) // 후기 생성
    public ResponseDto<?> contentTest(
            @RequestPart(value = "contents" ) CreateReviewDto createReviewDto)
    {

        return ResponseDto.ok("콘텐츠 전달 완료");
    }

    /*방문 후기 작성하기*/
    @PostMapping(value = "/w/certi", consumes = {MediaType.APPLICATION_JSON_VALUE , MediaType.MULTIPART_FORM_DATA_VALUE}) // 후기 생성
    public ResponseDto<?> createCertifiedReview(
            @UserId Long userId,
            @RequestParam("fcmToken") String token,
            @RequestParam("popupId") Long popupId,
            @RequestParam("text") String text,
            @RequestParam("visitDate") String visitDate,
            @RequestParam("satisfaction") String satisfaction,
            @RequestParam("congestion") String congestion,
            @RequestParam("nickname") String nickname,
            @RequestPart(value = "images" ) List<MultipartFile> images)
    {

        return ResponseDto.ok(reviewService.writeCertifiedReview(userId, token, popupId, text, visitDate, satisfaction, congestion, nickname, images));
    }

    /*일반 후기 작성*/
    @PostMapping(value = "/w/uncerti", consumes = {MediaType.APPLICATION_JSON_VALUE , MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> createUncertiReview(
            @UserId Long userId,
            @RequestParam("fcmToken") String token,
            @RequestParam("popupId") Long popupId,
            @RequestParam("text") String text,
            @RequestParam("visitDate") String visitDate,
            @RequestParam("satisfaction") String satisfaction,
            @RequestParam("congestion") String congestion,
            @RequestParam("nickname") String nickname,
            @RequestPart(value = "images" ) List<MultipartFile> images)
    {
        return ResponseDto.ok(reviewService.writeUncertifiedReview(userId, token, popupId, text, visitDate, satisfaction, congestion, nickname, images));
    }

    @PostMapping("/add-recommend") // 후기 추천
    public ResponseDto<?> addRecommendReview(@UserId Long userId,
                                             @RequestParam("reviewId") Long reviewId,
                                             @RequestParam("popupId") Long popupId
                                             )
    {
        return ResponseDto.ok(reviewService.addRecommendReview(userId, reviewId, popupId));
    }

}
