package com.poppin.poppinserver.popup.controller;

import com.poppin.poppinserver.core.dto.ResponseDto;
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

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BootstrapController {
    private final PopupService popupService;
    private final AuthService authService;

    // 앱 진입 시 버전 확인
    @PostMapping("/app/start")
    public ResponseDto<?> appStart(@RequestBody @Valid AppStartRequestDto appStartRequestDto) {
        return ResponseDto.ok(authService.appStart(appStartRequestDto));
    }

    @GetMapping("/bootstrap")
    public ResponseDto<?> bootstrap(HttpServletRequest request) {
        log.info("Bootstrap request received");
        return ResponseDto.ok(popupService.bootstrap(request));
    }
}
