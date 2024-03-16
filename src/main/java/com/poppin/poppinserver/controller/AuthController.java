package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.constant.Constant;
import com.poppin.poppinserver.dto.auth.request.PasswordRequestDto;
import com.poppin.poppinserver.dto.auth.request.SocialRegisterRequestDto;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.auth.request.AuthSignUpDto;
import com.poppin.poppinserver.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
        return ResponseDto.created("회원 가입 성공");
    }

    @PostMapping("/register")
    public ResponseDto<?> socialRegister(@NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken,
                                         @RequestBody @Valid SocialRegisterRequestDto socialRegisterRequestDto) {
        log.info("socialRegisterRequestDto : " + socialRegisterRequestDto);
        return ResponseDto.created(authService.socialRegister(accessToken, socialRegisterRequestDto));
    }

    @PostMapping("/login/kakao")
    public ResponseDto<?> authKakaoLogin(@NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken) {
        log.info("accessToken : " + accessToken);   // 'bearer ' 제거 필요
        return ResponseDto.ok(authService.authKakaoLogin(accessToken));
    }

    @PostMapping("/login/naver")
    public ResponseDto<?> authNaverLogin(@NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken) {
        return ResponseDto.ok(authService.authNaverLogin(accessToken));
    }

    @PostMapping("/login/google")
    public ResponseDto<?> authGoogleLogin(@NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken) {
        return ResponseDto.ok(authService.authGoogleLogin(accessToken));
    }

    @PostMapping("/login/apple")
    public ResponseDto<?> authAppleLogin(@NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String idToken) {
        return ResponseDto.ok(authService.authAppleLogin(idToken));
    }

    @PutMapping("/reset-password")
    public ResponseDto<?> resetPassword(@UserId Long userId, @RequestBody @Valid PasswordRequestDto passwordRequestDto) {
        authService.resetPassword(userId, passwordRequestDto);
        return ResponseDto.ok("비밀번호 변경 성공");
    }
}
