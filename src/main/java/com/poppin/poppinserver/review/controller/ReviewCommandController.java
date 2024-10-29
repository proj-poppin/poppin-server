package com.poppin.poppinserver.review.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.review.service.ReviewRecommendService;
import com.poppin.poppinserver.review.service.ReviewWriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.write.enabled", havingValue = "true")
@RequestMapping("/api/v1/reviews")
public class ReviewCommandController {

    private final ReviewWriteService reviewWriteService;
    private final ReviewRecommendService reviewRecommendService;

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

        return ResponseDto.ok(reviewWriteService.writeReview(userId, popupId, text, visitDate, satisfaction, congestion, images));
    }

    /*후기 추천*/
    @PostMapping("/recommend")
    public ResponseDto<?> recommendReview(@UserId Long userId,
                                          @RequestParam("reviewId") String reviewId,
                                          @RequestParam("popupId") String popupId
    ) {
        return ResponseDto.ok(reviewRecommendService.recommendReview(userId, reviewId, popupId));
    }

}
