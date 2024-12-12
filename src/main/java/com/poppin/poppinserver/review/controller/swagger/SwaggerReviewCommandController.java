package com.poppin.poppinserver.review.controller.swagger;

import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.review.dto.response.ReviewDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "후기 관리", description = "후기 작성 및 추천 관리 API")
public interface SwaggerReviewCommandController {

    @Operation(summary = "후기 작성", description = "사용자가 후기를 작성합니다.")
    @PostMapping(value = "/write", consumes = {"application/json", "multipart/form-data"})
    ResponseDto<ReviewDto> writeReview(
            @Parameter(hidden = true) Long userId,
            @RequestParam("popupId") String popupId,
            @RequestParam("text") String text,
            @RequestParam("visitDate") String visitDate,
            @RequestParam("satisfaction") String satisfaction,
            @RequestParam("congestion") String congestion,
            @RequestPart(value = "images") List<MultipartFile> images
    );

    @Operation(summary = "후기 추천", description = "사용자가 특정 후기를 추천합니다.")
    @PostMapping("/recommend")
    ResponseDto<String> recommendReview(
            @Parameter(hidden = true) Long userId,
            @RequestParam("reviewId") String reviewId,
            @RequestParam("popupId") String popupId
    );
}
