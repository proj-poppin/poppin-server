package com.poppin.poppinserver.visit.controller;

import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.visit.service.VisitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO: 컨트롤러 삭제 예정
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/rtvisit")
public class VisitController {

    private final VisitService visitService;

    @GetMapping("/show-visitors")
    public ResponseDto<?> getRealTimeVisitorsCnt(@RequestParam("popupId") Long popupId) {
        return ResponseDto.ok(visitService.showRealTimeVisitors(popupId));
    }



}
