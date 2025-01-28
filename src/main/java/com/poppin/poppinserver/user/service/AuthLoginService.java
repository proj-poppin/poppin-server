package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import com.poppin.poppinserver.alarm.usecase.TokenCommandUseCase;
import com.poppin.poppinserver.core.constant.Constant;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.util.HeaderUtil;
import com.poppin.poppinserver.core.util.JwtUtil;
import com.poppin.poppinserver.core.util.OAuth2Util;
import com.poppin.poppinserver.popup.dto.popup.response.PopupActivityResponseDto;
import com.poppin.poppinserver.popup.service.BlockedPopupService;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.domain.type.ELoginProvider;
import com.poppin.poppinserver.user.domain.type.EUserRole;
import com.poppin.poppinserver.user.dto.auth.request.AppleUserIdRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.AuthLoginRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.FcmTokenRequestDto;
import com.poppin.poppinserver.user.dto.auth.response.JwtTokenDto;
import com.poppin.poppinserver.user.dto.auth.response.OAuth2UserInfo;
import com.poppin.poppinserver.user.dto.user.response.UserActivityResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserInfoResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserNoticeResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserNotificationResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserPreferenceSettingDto;
import com.poppin.poppinserver.user.dto.user.response.UserRelationDto;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 소셜 로그인과 일반 로그인을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthLoginService {
    private final UserQueryUseCase userQueryUseCase;
    private final OAuth2Util oAuth2Util;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthService authService;

    // JWT, FCM 토큰 관련 서비스
    private final JwtUtil jwtUtil;
    private final TokenCommandUseCase tokenCommandUseCase;

    // 유저 알람 설정 서비스
    private final UserAlarmSettingService userAlarmSettingService;

    // 유저 취향 설정 서비스
    private final UserPreferenceSettingService userPreferenceSettingService;

    // 유저, 팝업 차단 서비스
    private final BlockUserService blockUserService;
    private final BlockedPopupService blockPopupService;

    // 유저 활동 정보 서비스
    private final UserActivityService userActivityService;

    // 로그인 제공자 파싱
    private ELoginProvider parseProvider(String provider) {
        try {
            return ELoginProvider.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommonException(ErrorCode.INVALID_OAUTH2_PROVIDER);
        }
    }

    // 소셜 로그인
    public Object authSocialLogin(String token, String provider, FcmTokenRequestDto fcmTokenRequestDto) {
        String accessToken = authService.refineToken(token);

        ELoginProvider loginProvider = parseProvider(provider);
        log.info("loginProvider : {}", loginProvider);

        OAuth2UserInfo oAuth2UserInfoDto = getOAuth2UserInfo(loginProvider, accessToken);

        return processUserLogin(oAuth2UserInfoDto, fcmTokenRequestDto.fcmToken(), loginProvider);
    }

    // Apple 소셜 로그인
    public Object appleSocialLogin(AppleUserIdRequestDto appleUserIdRequestDto) {
        User user = userQueryUseCase.findUserByEmail(appleUserIdRequestDto.appleUserId());

        OAuth2UserInfo oAuth2UserInfoDto = new OAuth2UserInfo(appleUserIdRequestDto.appleUserId(), user.getEmail());

        return processUserLogin(oAuth2UserInfoDto, appleUserIdRequestDto.fcmToken(), ELoginProvider.APPLE);
    }

    // OAuth2 사용자 정보 가져오기
    private OAuth2UserInfo getOAuth2UserInfo(ELoginProvider provider, String accessToken) {
        return switch (provider) {
            case KAKAO -> oAuth2Util.getKakaoUserInfo(accessToken);
            case NAVER -> oAuth2Util.getNaverUserInfo(accessToken);
            case GOOGLE -> oAuth2Util.getGoogleUserInfo(accessToken);
            default -> throw new CommonException(ErrorCode.INVALID_OAUTH2_PROVIDER);
        };
    }

    // 로그인 프로세스
    private Object processUserLogin(OAuth2UserInfo oAuth2UserInfo, String fcmToken, ELoginProvider provider) {
        User user = userQueryUseCase.findUserByEmailAndRole(oAuth2UserInfo.email(), EUserRole.USER);
        if (user.getProvider() != provider) {
            throw new CommonException(ErrorCode.DUPLICATED_SOCIAL_ID);
        }

        // 이메일 & USER 권한으로 조회한 결과가 있으면 기존 사용자 로그인
        return handleExistingUserLogin(user, fcmToken);
    }

    // 기존 사용자 로그인 처리
    private Object handleExistingUserLogin(User user, String fcmToken) {
        JwtTokenDto jwtTokenDto = jwtUtil.generateToken(user.getId(), EUserRole.USER);
        user.updateRefreshToken(jwtTokenDto.refreshToken());

        // 로그인 후 필요한 데이터를 생성하고 반환
        return buildUserInfoResponse(user, fcmToken);
    }

    // Header에 BASIC으로 로그인
    public UserInfoResponseDto authSignIn(String authorizationHeader, FcmTokenRequestDto fcmTokenRequestDto) {
        String[] credentials = decodeBasicAuth(authorizationHeader);
        String email = credentials[0];
        String password = credentials[1];
        String fcmToken = fcmTokenRequestDto.fcmToken();

        User user = authenticateUser(email, password);

        return buildUserInfoResponse(user, fcmToken);
    }

    private String[] decodeBasicAuth(String authorizationHeader) {
        String encoded = HeaderUtil.refineHeader(authorizationHeader, Constant.BASIC_PREFIX);
        String decoded = new String(Base64.getDecoder().decode(encoded));
        String[] credentials = decoded.split(":");

        if (credentials.length != 2) {
            throw new CommonException(ErrorCode.INVALID_AUTHORIZATION_HEADER);
        }

        return credentials;
    }

    private User authenticateUser(String email, String password) {
        User user = userQueryUseCase.findUserByEmail(email);

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new CommonException(ErrorCode.INVALID_LOGIN);
        }

        return user;
    }

    // Request Body에 email, password 받는 버전
    public UserInfoResponseDto authLogin(AuthLoginRequestDto authLoginRequestDto) {
        String email = authLoginRequestDto.email();
        String password = authLoginRequestDto.password();
        String fcmToken = authLoginRequestDto.fcmToken();

        User user = authenticateUser(email, password);

        return buildUserInfoResponse(user, fcmToken);
    }

    private UserInfoResponseDto buildUserInfoResponse(User user, String fcmToken) {
        AlarmSetting alarmSetting = userAlarmSettingService.getUserAlarmSetting(user);

        Long userId = user.getId();

        // FCM 토큰 검증
        tokenCommandUseCase.refreshFCMToken(user, fcmToken);

        // 리프레시 토큰 업데이트
        JwtTokenDto jwtTokenDto = jwtUtil.generateToken(userId, user.getRole());
        user.updateRefreshToken(jwtTokenDto.refreshToken());

        // 유저 취향 설정 정보 조회
        boolean isPreferenceSettingCreated = userPreferenceSettingService
                .readUserPreferenceSettingCreated(userId);
        UserPreferenceSettingDto userPreferenceSettingDto = userPreferenceSettingService
                .readUserPreference(userId);

        // 유저 공지사항 알림 정보 조회
        UserNoticeResponseDto userNoticeResponseDto = userActivityService.getUserNotificationStatus(userId);

        // 유저 알림 정보 조회
        UserNotificationResponseDto userNotificationResponseDto = userActivityService.getUserNotificationActivity(user);

        // 유저 팝업 정보 조회
        PopupActivityResponseDto popupActivityResponseDto = userActivityService.getPopupActivity(user);

        // 유저 활동 정보 조회
        UserActivityResponseDto userActivities = UserActivityResponseDto.fromProperties(
                popupActivityResponseDto,
                userNotificationResponseDto
        );

        // 차단 정보 조회
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
}
