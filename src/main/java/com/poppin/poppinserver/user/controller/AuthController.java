package com.poppin.poppinserver.user.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.constant.Constant;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.user.controller.swagger.SwaggerAuthController;
import com.poppin.poppinserver.user.dto.auth.request.AccountRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.AppleUserIdRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.AuthLoginRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.AuthSignUpRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.EmailVerificationRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.FcmTokenRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.PasswordResetRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.PasswordUpdateRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.PasswordVerificationRequestDto;
import com.poppin.poppinserver.user.dto.auth.response.AccountStatusResponseDto;
import com.poppin.poppinserver.user.dto.auth.response.AuthCodeResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserInfoResponseDto;
import com.poppin.poppinserver.user.service.AuthLoginService;
import com.poppin.poppinserver.user.service.AuthService;
import com.poppin.poppinserver.user.service.AuthSignUpService;
import com.poppin.poppinserver.user.service.UserPasswordService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class AuthController implements SwaggerAuthController {
    private final AuthService authService;
    private final AuthSignUpService authSignUpService;
    private final AuthLoginService authLoginService;
    private final UserPasswordService userPasswordService;

    // 계정 상태 반환 API
    @PostMapping("/account/status")
    public ResponseDto<AccountStatusResponseDto> getAccountStatus(
            @RequestBody @Valid AccountRequestDto accountRequestDto
    ) {
        return ResponseDto.ok(authService.getAccountStatus(accountRequestDto));
    }

    // 계정 상태 반환 API - Apple
    @PostMapping("/account/status/apple")
    public ResponseDto<AccountStatusResponseDto> getAppleAccountStatus(
            @RequestBody @Valid AppleUserIdRequestDto appleUserIdRequestDto
    ) {
        return ResponseDto.ok(authService.getAppleAccountStatus(appleUserIdRequestDto));
    }

    // 자체 회원가입 API
    @PostMapping("/sign-up")
    public ResponseDto<UserInfoResponseDto> authSignUp(
            @RequestBody @Valid AuthSignUpRequestDto authSignUpRequestDto
    ) {
        return ResponseDto.created(authSignUpService.handleSignUp(authSignUpRequestDto));
    }

    // TODO: 삭제 예정 - 소셜 회원가입 방식 변경
//    @PostMapping("/register")
//    public ResponseDto<?> socialRegister(@NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken,
//                                         @RequestBody @Valid SocialRegisterRequestDto socialRegisterRequestDto) {
//        log.info("socialRegisterRequestDto : " + socialRegisterRequestDto);
//        return ResponseDto.created(authService.socialRegister(accessToken, socialRegisterRequestDto));
//    }

    // TODO: body 로그인에서 base64 인코딩 로그인으로 전환 예정
    // base64 인코딩 로그인
//    @PostMapping("/sign-in")
//    public ResponseDto<?> authSignIn(
//            @NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String authorizationHeader,
//            @RequestBody @Valid FcmTokenRequestDto fcmTokenRequestDto
//    ) {
//        return ResponseDto.ok(authService.authSignIn(authorizationHeader, fcmTokenRequestDto));
//    }

    // body로 email, password 로그인
    @PostMapping("/sign-in")
    public ResponseDto<UserInfoResponseDto> authLogin(
            @RequestBody @Valid AuthLoginRequestDto authLoginRequestDto
    ) {
        return ResponseDto.ok(authLoginService.authLogin(authLoginRequestDto));
    }

    @PostMapping("/login/{provider}")
    public ResponseDto<?> authSocialLogin(
            @PathVariable String provider,
            @NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken,
            @RequestBody @Valid FcmTokenRequestDto fcmTokenRequestDto
    ) {
        log.info("accessToken : {}", accessToken);   // 'bearer ' 제거 필요
        return ResponseDto.ok(authLoginService.authSocialLogin(accessToken, provider, fcmTokenRequestDto));
    }

    @PostMapping("/login/apple")
    public ResponseDto<?> appleSocialLogin(
            @RequestBody @Valid AppleUserIdRequestDto appleUserIdRequestDto
    ) {
        log.info("apple login fcm token: {}", appleUserIdRequestDto.fcmToken());
        return ResponseDto.ok(authLoginService.appleSocialLogin(appleUserIdRequestDto));
    }

    @PostMapping("/refresh")
    public ResponseDto<UserInfoResponseDto> refresh(
            @NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String refreshToken,
            @RequestBody @Valid FcmTokenRequestDto fcmTokenRequestDto
    ) {
        return ResponseDto.ok(authService.refresh(refreshToken, fcmTokenRequestDto));
    }

    @PostMapping("/reset-password/no-auth")
    public ResponseDto<String> resetPasswordNoAuth(
            @RequestBody @Valid PasswordResetRequestDto passwordResetRequestDto) {
        userPasswordService.resetPasswordNoAuth(passwordResetRequestDto);
        return ResponseDto.ok("비밀번호가 재설정되었습니다.");
    }

    @PostMapping("/verification/password")
    public ResponseDto<Boolean> verifyPassword(
            @UserId Long userId,
            @RequestBody @Valid PasswordVerificationRequestDto passwordVerificationRequestDto
    ) {
        return ResponseDto.ok(userPasswordService.verifyPassword(userId, passwordVerificationRequestDto));
    }

    @PutMapping("/reset-password")
    public ResponseDto<Boolean> resetPassword(
            @UserId Long userId,
            @RequestBody @Valid PasswordUpdateRequestDto passwordRequestDto
    ) {
        userPasswordService.resetPassword(userId, passwordRequestDto);
        return ResponseDto.ok(Boolean.TRUE);
    }

    @PostMapping("/email/verification")
    public ResponseDto<AuthCodeResponseDto> sendEmailVerificationCode(
            @RequestBody @Valid EmailVerificationRequestDto emailVerificationRequestDto) {
        return ResponseDto.ok(authService.sendEmailVerificationCode(emailVerificationRequestDto));
    }

    //TODO: 삭제 예정 - API 통합
    //    @PostMapping("/email/verification/password")
//    public ResponseDto<?> sendPasswordResetVerificationEmail(
//            @RequestBody @Valid EmailVerificationRequestDto emailVerificationRequestDto) {
//        return ResponseDto.ok(authService.sendPasswordResetVerificationEmail(emailVerificationRequestDto));
//    }
}
