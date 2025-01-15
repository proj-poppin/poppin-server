package com.poppin.poppinserver.user.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// 프론트 단에서 검증, 서버 단 5종 회원가입 통합으로 인해 DTO단 검증 불가
public record AuthSignUpRequestDto(
        // 이메일
        // @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식이 아닙니다.")
        String email,

        // 애플 유저 아이디
        String appleUserId,

        // 영문/숫자 둘 중 하나 이상 반드시 포함, 특수문자 반드시 포함, 8자 이상
        // @Pattern(regexp = "^(?=.*[A-Za-z\\d])(?=.*[!@#$%^&*()]).{8,}$", message = "올바른 비밀번호 형식이 아닙니다.")
        String password,

        String passwordConfirm,

        // 한글/영문 1자 이상 10자 이하(공백 포함), 공백만 입력 불가
        @NotEmpty
        @Size(min = 1, max = 10)
        @Pattern(regexp = "^(?=.*[가-힣A-Za-z])[가-힣A-Za-z\\s]*$", message = "올바른 닉네임 형식이 아닙니다.")
        String nickname,

        @NotNull(message = "fcmToken is required.")
        @Pattern(
                regexp = "^[a-zA-Z0-9\\-_:]{120,}$",
                message = "올바른 FCM 토큰 형식이 아닙니다."
        )
        String fcmToken,

        // KAKAO, NAVER, GOOGLE, APPLE, DEFAULT
        @NotBlank(message = "accountType is required.")
        String accountType,

        // 개인정보 보호정책 동의 여부
        @NotNull(message = "개인정보 보호정책 동의가 필요합니다.")
        Boolean agreedToPrivacyPolicy,

        // 서비스 이용 약관
        @NotNull(message = "서비스 이용 약관 동의가 필요합니다.")
        Boolean agreedToServiceTerms
) {
}
