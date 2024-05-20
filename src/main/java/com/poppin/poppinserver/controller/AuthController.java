package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.constant.Constant;
import com.poppin.poppinserver.dto.auth.request.EmailRequestDto;
import com.poppin.poppinserver.dto.auth.request.PasswordRequestDto;
import com.poppin.poppinserver.dto.auth.request.SocialRegisterRequestDto;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.auth.request.AuthSignUpDto;
import com.poppin.poppinserver.service.AuthService;
import com.poppin.poppinserver.type.ELoginProvider;
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
        log.info("authSignUpDto : " + authSignUpDto);
        return ResponseDto.created(authService.authSignUp(authSignUpDto));
    }

    @PostMapping("/register")
    public ResponseDto<?> socialRegister(@NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken,
                                         @RequestBody @Valid SocialRegisterRequestDto socialRegisterRequestDto) {
        log.info("socialRegisterRequestDto : " + socialRegisterRequestDto);
        return ResponseDto.created(authService.socialRegister(accessToken, socialRegisterRequestDto));
    }

    @PostMapping("/sign-in")
    public ResponseDto<?> authSignIn(@NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String authorizationHeader) {
        return ResponseDto.ok(authService.authSignIn(authorizationHeader));
    }

//    @PostMapping("/login/kakao")
//    public ResponseDto<?> authKakaoLogin(@NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken) {
//
//        return ResponseDto.ok(authService.authKakaoLogin(accessToken));
//    }
//
//    @PostMapping("/login/naver")
//    public ResponseDto<?> authNaverLogin(@NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken) {
//        return ResponseDto.ok(authService.authNaverLogin(accessToken));
//    }
//
//    @PostMapping("/login/google")
//    public ResponseDto<?> authGoogleLogin(@NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken) {
//        return ResponseDto.ok(authService.authGoogleLogin(accessToken));
//    }
//
//    @PostMapping("/login/apple")
//    public ResponseDto<?> authAppleLogin(@NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String idToken) {
//        return ResponseDto.ok(authService.authAppleLogin(idToken));
//    }

    @PostMapping("/login/{provider}")
    public ResponseDto<?> authSocialLogin(@PathVariable String provider,
                                          @NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken) {
        log.info("accessToken : " + accessToken);   // 'bearer ' 제거 필요
        return ResponseDto.ok(authService.authSocialLogin(accessToken, provider));
    }

    @PostMapping("/refresh")
    public ResponseDto<?> refresh(
            @NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String refreshToken){
        return ResponseDto.ok(authService.refresh(refreshToken));
    }

    @PutMapping("/reset-password")
    public ResponseDto<?> resetPassword(@UserId Long userId, @RequestBody @Valid PasswordRequestDto passwordRequestDto) {
        authService.resetPassword(userId, passwordRequestDto);
        return ResponseDto.ok("비밀번호 변경 성공");
    }

    @PostMapping("/email/verification/password")
    public ResponseDto<?> sendPasswordResetVerificationEmail(@RequestBody @Valid EmailRequestDto emailRequestDto) {
        return ResponseDto.ok(authService.sendPasswordResetVerificationEmail(emailRequestDto));
    }

    @PostMapping("/email/verification")
    public ResponseDto<?> sendSignUpEmail(@RequestBody @Valid EmailRequestDto emailRequestDto) {
        return ResponseDto.ok(authService.sendSignUpEmail(emailRequestDto));
    }
}
