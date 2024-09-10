package com.poppin.poppinserver.visit.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.popup.dto.popup.request.VisitorsInfoDto;
import com.poppin.poppinserver.visit.service.VisitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/add-visitors") /*방문하기*/
    public ResponseDto<?> addRealTimeVisitors(@UserId Long userId, @RequestBody VisitorsInfoDto visitorsInfoDto) {
        return ResponseDto.ok(visitService.addRealTimeVisitors(userId, visitorsInfoDto));
    }

}
