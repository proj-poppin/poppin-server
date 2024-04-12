package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.popup.request.PopupInfoDto;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.service.VisitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/rtvisit")
public class VisitorController {

    private final VisitorService visitorService;

    @GetMapping ("/show-visitors")
    public ResponseDto<?> getRealTimeVisitorsCnt(@RequestParam("popupId") Long popupId){ return ResponseDto.ok(visitorService.showRealTimeVisitors(popupId));}

    @PostMapping("/add-visitors")
    public  ResponseDto<?> addRealTimeVisitors(@UserId Long userId, PopupInfoDto popupInfoDto){return ResponseDto.ok(visitorService.addRealTimeVisitors(userId,popupInfoDto));}

}
