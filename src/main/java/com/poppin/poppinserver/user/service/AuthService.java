package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import com.poppin.poppinserver.alarm.usecase.TokenCommandUseCase;
import com.poppin.poppinserver.core.constant.Constant;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.util.JwtUtil;
import com.poppin.poppinserver.core.util.RandomCodeUtil;
import com.poppin.poppinserver.popup.dto.popup.response.PopupActivityResponseDto;
import com.poppin.poppinserver.popup.service.BlockedPopupService;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.domain.type.EAccountStatus;
import com.poppin.poppinserver.user.domain.type.EVerificationType;
import com.poppin.poppinserver.user.dto.auth.request.AccountRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.AppStartRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.AppleUserIdRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.EmailVerificationRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.FcmTokenRequestDto;
import com.poppin.poppinserver.user.dto.auth.response.AccountStatusResponseDto;
import com.poppin.poppinserver.user.dto.auth.response.AuthCodeResponseDto;
import com.poppin.poppinserver.user.dto.auth.response.JwtTokenDto;
import com.poppin.poppinserver.user.dto.user.response.UserActivityResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserInfoResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserNoticeResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserNotificationResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserPreferenceSettingDto;
import com.poppin.poppinserver.user.dto.user.response.UserRelationDto;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final MailService mailService;
    private final UserQueryUseCase userQueryUseCase;

    // JWT, FCM 토큰 관련 서비스
    private final TokenCommandUseCase tokenCommandUseCase;
    private final JwtUtil jwtUtil;

    // 유저 알람 설정 서비스
    private final UserAlarmSettingService userAlarmSettingService;

    // 유저 취향 설정 서비스
    private final UserPreferenceSettingService userPreferenceSettingService;

    // 차단
    private final BlockUserService blockUserService;
    private final BlockedPopupService blockPopupService;

    // 유저의 활동 내역
    private final UserActivityService userActivityService;

    // 이메일 확인 코드 전송 메서드
    public AuthCodeResponseDto sendEmailVerificationCode(EmailVerificationRequestDto emailVerificationRequestDto) {
        EVerificationType verificationType = EVerificationType.valueOf(
                emailVerificationRequestDto.verificationType().toUpperCase()
        );

        validateEmail(verificationType, emailVerificationRequestDto.email());

        String authCode = RandomCodeUtil.generateVerificationCode();
        mailService.sendEmail(emailVerificationRequestDto.email(), "[Poppin] 이메일 인증코드", authCode);

        return AuthCodeResponseDto.fromAuthCode(authCode);
    }

    private void validateEmail(EVerificationType verificationType, String email) {
        boolean userExists = userQueryUseCase.findUserByEmailOptional(email).isPresent();

        if (verificationType.equals(EVerificationType.SIGN_UP) && userExists) {
            // 회원가입 시에 이메일 중복 -> 중복 이메일 Exception 반환
            throw new CommonException(ErrorCode.DUPLICATED_EMAIL);
        } else if (verificationType.equals(EVerificationType.PASSWORD_RESET) && !userExists) {
            // 비밀번호 재설정 시에 이메일 없음 -> 이메일 없음 Exception 반환
            throw new CommonException(ErrorCode.NOT_FOUND_USER);
        }
    }

    // 토큰 재발급 메서드 (자동 로그인)
    @Transactional
    public UserInfoResponseDto refresh(String refreshToken, FcmTokenRequestDto fcmTokenRequestDto) {
        String token = refineToken(refreshToken);
        String fcmToken = fcmTokenRequestDto.fcmToken();

        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userQueryUseCase.findUserById(userId);

        if (!user.getRefreshToken().equals(token)) {
            throw new CommonException(ErrorCode.INVALID_TOKEN_ERROR);
        }

        JwtTokenDto jwtTokenDto = jwtUtil.generateToken(userId, user.getRole());
        AlarmSetting alarmSetting = userAlarmSettingService.getUserAlarmSetting(user);

        // FCM 토큰 검증
        tokenCommandUseCase.refreshFCMToken(user, fcmToken);
        user.updateRefreshToken(jwtTokenDto.refreshToken());

        boolean isPreferenceSettingCreated = userPreferenceSettingService
                .readUserPreferenceSettingCreated(user.getId());
        UserPreferenceSettingDto userPreferenceSettingDto = userPreferenceSettingService.readUserPreference(
                user.getId()
        );

        UserNoticeResponseDto userNoticeResponseDto = userActivityService.getUserNotificationStatus(user.getId());
        UserNotificationResponseDto userNotificationResponseDto = userActivityService.getUserNotificationActivity(
                user, fcmToken
        );

        PopupActivityResponseDto popupActivityResponseDto = userActivityService.getPopupActivity(user);

        UserActivityResponseDto userActivities = UserActivityResponseDto.fromProperties(
                popupActivityResponseDto,
                userNotificationResponseDto
        );

        List<String> blockedPopups = blockPopupService.findBlockedPopupList(user);
        List<String> blockedUsers = blockUserService.findBlockedUserList(user);

        UserRelationDto userRelationDto = UserRelationDto.ofBlockedUserIdsAndPopupIds(blockedUsers, blockedPopups);

        return UserInfoResponseDto.fromUserEntity(
                user,
                alarmSetting,
                jwtTokenDto,
                userPreferenceSettingDto,
                userNoticeResponseDto,
                userActivities,
                userRelationDto,
                isPreferenceSettingCreated
        );
    }

    // OS와 앱 버전 확인 메서드
    public Boolean appStart(AppStartRequestDto appStartRequestDto) {
        String platform = appStartRequestDto.os();
        String appVersion = appStartRequestDto.appVersion();
        if (platform.equals(Constant.iOS) && appVersion.equals(Constant.iOS_APP_VERSION)) {
            return Boolean.TRUE;
        }
        if (platform.equals(Constant.ANDROID) && appVersion.equals(Constant.ANDROID_APP_VERSION)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Transactional(readOnly = true)
    public AccountStatusResponseDto getAccountStatus(AccountRequestDto accountRequestDto) {
        return determineAccountStatus(accountRequestDto.email());
    }

    @Transactional(readOnly = true)
    public AccountStatusResponseDto getAppleAccountStatus(AppleUserIdRequestDto appleUserIdRequestDto) {
        return determineAccountStatus(appleUserIdRequestDto.appleUserId());
    }

    private AccountStatusResponseDto determineAccountStatus(String email) {
        EAccountStatus accountStatus = userQueryUseCase.findUserByEmailOptional(email)
                .map(user -> EAccountStatus.LOGIN)
                .orElse(EAccountStatus.SIGNUP);
        return AccountStatusResponseDto.fromEnum(accountStatus);
    }

    public String refineToken(String accessToken) {
        return accessToken.startsWith(Constant.BEARER_PREFIX)
                ? accessToken.substring(Constant.BEARER_PREFIX.length())
                : accessToken;
    }
}
