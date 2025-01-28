package com.poppin.poppinserver.core.dto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AppInfoController {
    @GetMapping("/constants")
    public ResponseDto<AppInfoResponseDto> constants() {
        return ResponseDto.ok(AppInfoResponseDto.fromConstants());
    }
}
