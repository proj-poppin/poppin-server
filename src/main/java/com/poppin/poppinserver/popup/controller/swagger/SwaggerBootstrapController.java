package com.poppin.poppinserver.popup.controller.swagger;

import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.popup.dto.popup.response.BootstrapDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "부스트스트랩", description = "부스트스트랩 관련 API")
public interface SwaggerBootstrapController {
    @Operation(summary = "부스트스트랩", description = "부스트스트랩 데이터를 가져옵니다.")
    @GetMapping("/bootstrap")
    ResponseDto<BootstrapDto> bootstrap(
            @Parameter(hidden = true) HttpServletRequest request
    );
}

