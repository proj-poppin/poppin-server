package com.poppin.poppinserver.interest.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.interest.dto.interest.request.InterestRequestDto;
import com.poppin.poppinserver.interest.service.InterestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/interest")
public class InterestController {
    private final InterestService interestService;

    @PostMapping("")
    public ResponseDto<?> addInterest(@UserId Long userId, @RequestBody InterestRequestDto interestRequestDto) {
        return ResponseDto.ok(interestService.userAddInterest(userId, interestRequestDto));
    }

    @DeleteMapping("")
    public ResponseDto<?> removeInterest(@UserId Long userId, @RequestBody InterestRequestDto interestRequestDto) {
        return ResponseDto.ok(interestService.removeInterest(userId, interestRequestDto));
    }
}
