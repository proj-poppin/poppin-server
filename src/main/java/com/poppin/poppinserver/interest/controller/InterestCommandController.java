package com.poppin.poppinserver.interest.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.interest.controller.swagger.SwaggerInterestCommandController;
import com.poppin.poppinserver.interest.dto.interest.request.InterestRequestDto;
import com.poppin.poppinserver.interest.dto.interest.response.InterestDto;
import com.poppin.poppinserver.interest.service.InterestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

// 관심팝업
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/interest")
public class InterestCommandController implements SwaggerInterestCommandController {
    private final InterestService interestService;

    //관심 등록
    @PostMapping("")
    public ResponseDto<InterestDto> addInterest(@UserId Long userId, @RequestBody InterestRequestDto interestRequestDto) throws FirebaseMessagingException {
        return ResponseDto.ok(interestService.userAddInterest(userId, interestRequestDto));
    }

    //관심 등록 취소
    @DeleteMapping("")
    public ResponseDto<InterestDto> removeInterest(@UserId Long userId, @RequestBody InterestRequestDto interestRequestDto) throws FirebaseMessagingException {
        return ResponseDto.ok(interestService.removeInterest(userId, interestRequestDto));
    }
}
