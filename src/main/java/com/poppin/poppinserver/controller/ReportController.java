package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.report.request.CreatePopupReportDto;
import com.poppin.poppinserver.dto.report.request.CreateReviewReportDto;
import com.poppin.poppinserver.dto.review.request.ReviewInfoDto;
import com.poppin.poppinserver.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    // 관리자 페이지 - 후기 신고 - 후기 숨기기
    @PostMapping("/hide-review")
    public ResponseDto<?> hideReview(@UserId Long userId, @RequestBody ReviewInfoDto reviewInfoDto){
        return ResponseDto.ok(reportService.hideReview(userId, reviewInfoDto));
    }

    /* 유저 후기 신고 */
    @PostMapping("/review/{reviewId}")
    public ResponseDto<?> createReviewReport(@UserId Long userId,
                                             @PathVariable Long reviewId,
                                             @RequestBody CreateReviewReportDto createReviewReportDto){
        reportService.createReviewReport(userId, reviewId, createReviewReportDto);
        return ResponseDto.ok("후기 신고가 접수되었습니다.");
    }

    /* 유저 팝업 신고 */
    @PostMapping("/popup/{popupId}")
    public ResponseDto<?> createPopupReport(@UserId Long userId,
                                            @PathVariable Long popupId,
                                            @RequestBody CreatePopupReportDto createPopupReportDto){
        reportService.createPopupReport(userId, popupId, createPopupReportDto);
        return ResponseDto.ok("팝업 신고가 접수되었습니다.");
    }

}
