package com.poppin.poppinserver.popup.controller;

import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.popup.controller.swagger.SwaggerBootstrapController;
import com.poppin.poppinserver.popup.dto.popup.response.BootstrapDto;
import com.poppin.poppinserver.popup.service.BootstrapService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 부스트스트랩
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BootstrapController implements SwaggerBootstrapController {
    private final BootstrapService bootstrapService;

    // 부스트스트랩
    @GetMapping("/bootstrap")
    public ResponseDto<BootstrapDto> bootstrap(HttpServletRequest request) {
        return ResponseDto.ok(bootstrapService.bootstrap(request));
    }
}
