package com.poppin.poppinserver.popup.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.popup.service.PopupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BootstrapController {
    private final PopupService popupService;

    @GetMapping("/bootstrap")
    public ResponseDto<?> bootstrap(@UserId Long userId) {
        return ResponseDto.ok(popupService.bootstrap(userId));
    }
}
