package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.interest.request.InterestRequestDto;
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
    public ResponseDto<?> addInterest(@UserId Long userId, @RequestBody InterestRequestDto interestRequestDto){
        log.info("userId : " + userId.toString());
        return ResponseDto.ok(interestService.userAddInterest(userId, interestRequestDto));
    }

    @DeleteMapping("/remove-interest")
    public ResponseDto<?> removeInterest( @UserId Long userId,  @RequestBody InterestRequestDto interestRequestDto){
        return ResponseDto.ok(interestService.removeInterest(userId, interestRequestDto));
    }
}
