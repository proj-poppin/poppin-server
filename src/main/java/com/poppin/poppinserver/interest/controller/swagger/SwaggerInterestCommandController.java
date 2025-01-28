package com.poppin.poppinserver.interest.controller.swagger;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.interest.dto.interest.request.InterestRequestDto;
import com.poppin.poppinserver.interest.dto.interest.response.InterestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "관심 팝업", description = "관심 팝업 관련 API")
public interface SwaggerInterestCommandController {

    @Operation(summary = "관심 등록", description = "사용자가 관심 팝업을 등록합니다.")
    @PostMapping("")
    ResponseDto<InterestDto> addInterest(
            @Parameter(hidden = true) Long userId,
            @RequestBody InterestRequestDto interestRequestDto
    ) throws FirebaseMessagingException;

    @Operation(summary = "관심 등록 취소", description = "사용자가 관심 팝업을 취소합니다.")
    @DeleteMapping("")
    ResponseDto<InterestDto> removeInterest(
            @Parameter(hidden = true) Long userId,
            @RequestBody InterestRequestDto interestRequestDto
    ) throws FirebaseMessagingException;
}
