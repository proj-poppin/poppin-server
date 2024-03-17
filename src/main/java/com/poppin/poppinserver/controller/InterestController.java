package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.interest.requeste.AddInterestDto;
import com.poppin.poppinserver.service.InterestService;
import jakarta.validation.Valid;
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
    public ResponseDto<?> addInterest(@RequestBody @Valid AddInterestDto addInterestDto, @UserId Long userId){
        log.info("userId : " + userId.toString());
        return ResponseDto.ok(interestService.userAddInterest(addInterestDto, userId));
    }

}
