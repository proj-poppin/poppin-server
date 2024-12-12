package com.poppin.poppinserver.visit.controller.swagger;

import com.poppin.poppinserver.core.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "방문자 관리", description = "방문자 관련 API")
public interface SwaggerVisitController {

    @Operation(summary = "실시간 방문자 조회", description = "특정 팝업의 실시간 방문자 수를 조회합니다.")
    @GetMapping("/show-visitors")
    ResponseDto<?> getRealTimeVisitorsCnt(@RequestParam("popupId") Long popupId);
}
