package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.review.request.ReviewInfoDto;
import com.poppin.poppinserver.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/complaint")
public class ReportController {

    private final ReportService reportService;

    // 관리자 페이지 - 후기 신고 - 후기 숨기기
    @PostMapping("/hide-review")
    public ResponseDto<?> hideReview(@UserId Long userId, @RequestBody ReviewInfoDto reviewInfoDto){
        return ResponseDto.ok(reportService.hideReview(userId, reviewInfoDto));
    }
}
