package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.popup.request.PopupInfoDto;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.service.VisitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/rtvisit")
public class VisitController {

    private final VisitService visitService;

    @GetMapping ("/show-visitors")
    public ResponseDto<?> getRealTimeVisitorsCnt(@RequestParam("popupId") Long popupId){ return ResponseDto.ok(visitService.showRealTimeVisitors(popupId));}

    @PostMapping("/add-visitors") /*방문하기*/
    public  ResponseDto<?> addRealTimeVisitors(@UserId Long userId, @RequestBody PopupInfoDto popupInfoDto){return ResponseDto.ok(visitService.addRealTimeVisitors(userId,popupInfoDto));}

}
