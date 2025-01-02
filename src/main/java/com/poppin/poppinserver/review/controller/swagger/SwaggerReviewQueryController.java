package com.poppin.poppinserver.review.controller.swagger;

import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.review.dto.response.ReviewDto;
import com.poppin.poppinserver.review.dto.response.ReviewListDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "후기 조회", description = "후기 조회 API")
public interface SwaggerReviewQueryController {

    @Operation(summary = "작성 완료 후기 리스트 조회", description = "팝업스토어에 대해 후기를 작성 완료한 리스트를 제공 합니다")
    @GetMapping("/list")
    ResponseDto<List<ReviewListDto>> readReviewList(
            @Parameter(hidden = true) Long userId
    );

    @GetMapping("/read")
    ResponseDto<ReviewDto> readReview(
            @Parameter(hidden = true) Long userId,
            @RequestParam("reviewId") String reviewId
    );
}
