package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.service.InterestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/interest")
public class InterestController {
    private final InterestService interestService;

    @PostMapping("/add-interest")
    public ResponseDto<?> addInterest(@RequestParam("popupId") Long popupId, @UserId Long userId, @RequestParam("fcm_token")String token){
        log.info("userId : " + userId.toString());
        return ResponseDto.ok(interestService.userAddInterest(popupId, userId, token));
    }

    @DeleteMapping("/remove-interest")
    public ResponseDto<?> removeInterest(@RequestParam("popupId") Long popupId, @UserId Long userId, @RequestParam("fcm_token")String token){
        return ResponseDto.ok(interestService.removeInterest(userId, popupId, token));
    }
}
