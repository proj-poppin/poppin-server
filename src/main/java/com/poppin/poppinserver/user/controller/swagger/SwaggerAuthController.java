package com.poppin.poppinserver.user.controller.swagger;

import com.poppin.poppinserver.core.constant.Constant;
import com.poppin.poppinserver.core.dto.ResponseDto;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "인증", description = "인증 관련 API")
public interface SwaggerAuthController {

    @Operation(summary = "계정 상태 조회", description = "계정 상태를 반환합니다.")
    @PostMapping("/account/status")
    ResponseDto<AccountStatusResponseDto> getAccountStatus(
            @RequestBody @Valid AccountRequestDto accountRequestDto
    );

    @Operation(summary = "애플 계정 상태 조회", description = "애플 계정 상태를 반환합니다.")
    @PostMapping("/account/status/apple")
    ResponseDto<AccountStatusResponseDto> getAppleAccountStatus(
            @RequestBody @Valid AppleUserIdRequestDto appleUserIdRequestDto
    );

    @Operation(summary = "회원가입", description = "자체 회원가입 API")
    @PostMapping("/sign-up")
    ResponseDto<UserInfoResponseDto> authSignUp(
            @RequestBody @Valid AuthSignUpRequestDto authSignUpRequestDto
    );

    @Operation(summary = "로그인", description = "이메일과 비밀번호를 사용한 로그인 API")
    @PostMapping("/sign-in")
    ResponseDto<UserInfoResponseDto> authLogin(
            @RequestBody @Valid AuthLoginRequestDto authLoginRequestDto
    );

    @Operation(summary = "소셜 로그인", description = "소셜 로그인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 완료, access token과 refresh token 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "201", description = "회원가입 필요, access token만 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))),
    })
    @PostMapping("/login/{provider}")
    ResponseDto<?> authSocialLogin(
            @PathVariable String provider,
            @NotNull @RequestHeader("Authorization") String accessToken,
            @Valid @RequestBody FcmTokenRequestDto fcmTokenRequestDto
    );

    @Operation(summary = "토큰 갱신", description = "Refresh Token을 사용하여 Access Token을 갱신합니다.")
    @PostMapping("/refresh")
    ResponseDto<UserInfoResponseDto> refresh(
            @NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String refreshToken,
            @RequestBody @Valid FcmTokenRequestDto fcmTokenRequestDto
    );

    @Operation(summary = "비밀번호 변경", description = "사용자의 비밀번호를 변경합니다.")
    @PutMapping("/reset-password")
    ResponseDto<Boolean> resetPassword(
            @Parameter(hidden = true) Long userId,
            @RequestBody @Valid PasswordUpdateRequestDto passwordRequestDto
    );

    @Operation(summary = "비회원 비밀번호 재설정", description = "인증 없이 비밀번호를 재설정합니다.")
    @PostMapping("/reset-password/no-auth")
    ResponseDto<String> resetPasswordNoAuth(
            @RequestBody @Valid PasswordResetRequestDto passwordResetRequestDto
    );

    @Operation(summary = "비밀번호 검증", description = "사용자의 비밀번호를 검증합니다.")
    @PostMapping("/verification/password")
    ResponseDto<Boolean> verifyPassword(
            @Parameter(hidden = true) Long userId,
            @RequestBody @Valid PasswordVerificationRequestDto passwordVerificationRequestDto
    );

    @Operation(summary = "이메일 인증 코드 전송", description = "이메일 인증 코드를 전송합니다.")
    @PostMapping("/email/verification")
    ResponseDto<AuthCodeResponseDto> sendEmailVerificationCode(
            @RequestBody @Valid EmailVerificationRequestDto emailVerificationRequestDto
    );
}
