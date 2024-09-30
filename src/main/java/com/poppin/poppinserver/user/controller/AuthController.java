package com.poppin.poppinserver.user.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.constant.Constant;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.user.dto.auth.request.AccountRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.AppStartRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.AuthSignUpDto;
import com.poppin.poppinserver.user.dto.auth.request.EmailVerificationRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.FcmTokenRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.PasswordResetDto;
import com.poppin.poppinserver.user.dto.auth.request.PasswordUpdateDto;
import com.poppin.poppinserver.user.dto.auth.request.PasswordVerificationDto;
import com.poppin.poppinserver.user.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    // 앱 진입 시
    @PostMapping("/app/start")
    public ResponseDto<?> appStart(@RequestBody @Valid AppStartRequestDto appStartRequestDto) {
        return ResponseDto.ok(authService.appStart(appStartRequestDto));
    }

    // 계정 상태 반환 API
    @GetMapping("/account/status")
    public ResponseDto<?> getAccountStatus(@RequestBody @Valid AccountRequestDto accountRequestDto) {
        return ResponseDto.ok(authService.getAccountStatus(accountRequestDto));
    }

    // 자체 회원가입 API
    @PostMapping("/sign-up")
    public ResponseDto<?> authSignUp(
            @RequestBody @Valid AuthSignUpDto authSignUpDto
    ) {
        log.info("authSignUpDto : " + authSignUpDto);
        return ResponseDto.created(authService.authSignUp(authSignUpDto));
    }

//    @PostMapping("/register")
//    public ResponseDto<?> socialRegister(@NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken,
//                                         @RequestBody @Valid SocialRegisterRequestDto socialRegisterRequestDto) {
//        log.info("socialRegisterRequestDto : " + socialRegisterRequestDto);
//        return ResponseDto.created(authService.socialRegister(accessToken, socialRegisterRequestDto));
//    }

    @PostMapping("/sign-in")
    public ResponseDto<?> authSignIn(
            @NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String authorizationHeader,
            @RequestBody @Valid FcmTokenRequestDto fcmTokenRequestDto
    ) {
        return ResponseDto.ok(authService.authSignIn(authorizationHeader, fcmTokenRequestDto));
    }

    @PostMapping("/login/{provider}")
    public ResponseDto<?> authSocialLogin(
            @PathVariable String provider,
            @NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken,
            @RequestBody @Valid FcmTokenRequestDto fcmTokenRequestDto
    ) {
        log.info("accessToken : " + accessToken);   // 'bearer ' 제거 필요
        return ResponseDto.ok(authService.authSocialLogin(accessToken, provider, fcmTokenRequestDto));
    }

    @PostMapping("/refresh")
    public ResponseDto<?> refresh(
            @NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String refreshToken) {
        return ResponseDto.ok(authService.refresh(refreshToken));
    }

    @PutMapping("/reset-password")
    public ResponseDto<?> resetPassword(@UserId Long userId, @RequestBody @Valid PasswordUpdateDto passwordRequestDto) {
        authService.resetPassword(userId, passwordRequestDto);
        return ResponseDto.ok("비밀번호 변경 성공");
    }

    @PostMapping("/reset-password/no-auth")
    public ResponseDto<?> resetPasswordNoAuth(@RequestBody @Valid PasswordResetDto passwordResetDto) {
        authService.resetPasswordNoAuth(passwordResetDto);
        return ResponseDto.ok("비밀번호가 재설정되었습니다.");
    }

    @PostMapping("/verification/password")
    public ResponseDto<?> verifyPassword(@UserId Long userId,
                                         @RequestBody @Valid PasswordVerificationDto passwordVerificationDto) {
        return ResponseDto.ok(authService.verifyPassword(userId, passwordVerificationDto));
    }

    @PostMapping("/email/verification")
    public ResponseDto<?> sendEmailVerificationCode(
            @RequestBody @Valid EmailVerificationRequestDto emailVerificationRequestDto) {
        return ResponseDto.ok(authService.sendEmailVerificationCode(emailVerificationRequestDto));
    }

    //    @PostMapping("/email/verification/password")
//    public ResponseDto<?> sendPasswordResetVerificationEmail(
//            @RequestBody @Valid EmailVerificationRequestDto emailVerificationRequestDto) {
//        return ResponseDto.ok(authService.sendPasswordResetVerificationEmail(emailVerificationRequestDto));
//    }
}
