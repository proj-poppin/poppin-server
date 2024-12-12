package com.poppin.poppinserver.popup.controller;

import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.popup.controller.swagger.SwaggerBootstrapController;
import com.poppin.poppinserver.popup.dto.popup.response.BootstrapDto;
import com.poppin.poppinserver.popup.service.BootstrapService;
import com.poppin.poppinserver.popup.service.PopupService;
import com.poppin.poppinserver.user.dto.auth.request.AppStartRequestDto;
import com.poppin.poppinserver.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//부스트스트랩
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BootstrapController implements SwaggerBootstrapController {
    private final PopupService popupService;
    private final BootstrapService bootstrapService;
    private final AuthService authService;

    // 앱 진입 시 버전 확인
    @PostMapping("/app/start")
    public ResponseDto<Boolean> appStart(@RequestBody @Valid AppStartRequestDto appStartRequestDto) {
        return ResponseDto.ok(authService.appStart(appStartRequestDto));
    }

    // 부스트스트랩
    @GetMapping("/bootstrap")
    public ResponseDto<BootstrapDto> bootstrap(HttpServletRequest request) {
        return ResponseDto.ok(bootstrapService.bootstrap(request));
    }
}
