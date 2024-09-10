package com.poppin.poppinserver.report.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.report.dto.report.request.CreatePopupReportDto;
import com.poppin.poppinserver.report.dto.report.request.CreateReviewReportDto;
import com.poppin.poppinserver.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    /* 유저 후기 신고 */
    @PostMapping("/review/{reviewId}")
    public ResponseDto<?> createReviewReport(@UserId Long userId,
                                             @PathVariable Long reviewId,
                                             @RequestBody CreateReviewReportDto createReviewReportDto) {
        reportService.createReviewReport(userId, reviewId, createReviewReportDto);
        return ResponseDto.created("후기 신고가 접수되었습니다.");
    }

    /* 유저 팝업 신고 */
    @PostMapping("/popup/{popupId}")
    public ResponseDto<?> createPopupReport(@UserId Long userId,
                                            @PathVariable Long popupId,
                                            @RequestBody CreatePopupReportDto createPopupReportDto) {
        reportService.createPopupReport(userId, popupId, createPopupReportDto);
        return ResponseDto.created("팝업 신고가 접수되었습니다.");
    }

}
