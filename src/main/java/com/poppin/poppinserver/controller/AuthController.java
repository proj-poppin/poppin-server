package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.auth.request.AuthSignUpDto;
import com.poppin.poppinserver.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    // 자체 회원가입 API
    @PostMapping("/sign-up")
    public ResponseDto<?> authSignUp(@RequestBody @Valid AuthSignUpDto authSignUpDto) {
        authService.authSignUp(authSignUpDto);
        log.info("authSignUpDto : " + authSignUpDto);
        return ResponseDto.created(null);
    }

}
