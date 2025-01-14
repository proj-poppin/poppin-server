package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import com.poppin.poppinserver.alarm.usecase.TokenCommandUseCase;
import com.poppin.poppinserver.core.constant.Constant;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.util.HeaderUtil;
import com.poppin.poppinserver.core.util.JwtUtil;
import com.poppin.poppinserver.core.util.OAuth2Util;
import com.poppin.poppinserver.core.util.PasswordUtil;
import com.poppin.poppinserver.popup.dto.popup.response.PopupActivityResponseDto;
import com.poppin.poppinserver.popup.service.BlockedPopupService;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.domain.type.ELoginProvider;
import com.poppin.poppinserver.user.domain.type.EUserRole;
import com.poppin.poppinserver.user.dto.auth.request.AppleUserIdRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.AuthLoginRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.FcmTokenRequestDto;
import com.poppin.poppinserver.user.dto.auth.response.AccessTokenDto;
import com.poppin.poppinserver.user.dto.auth.response.JwtTokenDto;
import com.poppin.poppinserver.user.dto.auth.response.OAuth2UserInfo;
import com.poppin.poppinserver.user.dto.user.response.UserActivityResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserInfoResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserNoticeResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserNotificationResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserPreferenceSettingDto;
import com.poppin.poppinserver.user.dto.user.response.UserRelationDto;
import com.poppin.poppinserver.user.repository.UserCommandRepository;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
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
    private final UserCommandRepository userCommandRepository;
    private final UserQueryRepository userQueryRepository;
    private final JwtUtil jwtUtil;
    private final TokenCommandUseCase tokenCommandUseCase;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthService authService;

    // 유저 알람 설정 정보
    private final UserAlarmSettingService userAlarmSettingService;

    // 유저 취향 설정 정보
    private final UserPreferenceSettingService userPreferenceSettingService;

    // 유저 차단 정보
    private final BlockUserService blockUserService;
    private final BlockedPopupService blockPopupService;

    // 유저 활동 정보
    private final UserActivityService userActivityService;

    public Object authSocialLogin(String token, String provider, FcmTokenRequestDto fcmTokenRequestDto) {
        String accessToken = authService.refineToken(token);
        String loginProvider = provider.toUpperCase();
        log.info("loginProvider : {}", loginProvider);
        OAuth2UserInfo oAuth2UserInfoDto = getOAuth2UserInfo(loginProvider, accessToken);

        return processUserLogin(
                oAuth2UserInfoDto,
                ELoginProvider.valueOf(loginProvider),
                fcmTokenRequestDto.fcmToken()
        );
    }

    public Object appleSocialLogin(AppleUserIdRequestDto appleUserIdRequestDto) {
        User user = userQueryUseCase.findUserByEmail(appleUserIdRequestDto.appleUserId());

        OAuth2UserInfo oAuth2UserInfoDto = new OAuth2UserInfo(
                appleUserIdRequestDto.appleUserId(),
                user.getEmail()
        );

        return processUserLogin(
                oAuth2UserInfoDto,
                ELoginProvider.APPLE,
                appleUserIdRequestDto.fcmToken()
        );
    }

    private OAuth2UserInfo getOAuth2UserInfo(String provider, String accessToken) {
        if (provider.equals(ELoginProvider.KAKAO.toString())) {
            return oAuth2Util.getKakaoUserInfo(accessToken);
        } else if (provider.equals(ELoginProvider.NAVER.toString())) {
            return oAuth2Util.getNaverUserInfo(accessToken);
        } else if (provider.equals(ELoginProvider.GOOGLE.toString())) {
            return oAuth2Util.getGoogleUserInfo(accessToken);
        } else {
            throw new CommonException(ErrorCode.INVALID_OAUTH2_PROVIDER);
        }
    }

    private Object processUserLogin(OAuth2UserInfo oAuth2UserInfo, ELoginProvider provider, String fcmToken) {
        Optional<User> user = userQueryUseCase.findUserByEmailAndRole(oAuth2UserInfo.email(), EUserRole.USER);

        // 회원 탈퇴 여부 확인
        user.filter(User::getIsDeleted)
                .ifPresent(u -> {
                    throw new CommonException(ErrorCode.DELETED_USER_ERROR);
                });

        // 이미 가입된 계정이 있는지 확인
        user.filter(u -> u.getProvider().equals(provider))
                .ifPresent(u -> {
                    throw new CommonException(ErrorCode.DUPLICATED_SOCIAL_ID);
                });

        // USER 권한 + 이메일 정보가 DB에 존재 -> 팝핀 토큰 발급 및 로그인 상태 변경
        if (user.isPresent()) {
            Long userId = user.get().getId();
            JwtTokenDto jwtTokenDto = jwtUtil.generateToken(user.get().getId(), EUserRole.USER);
            userCommandRepository.updateRefreshToken(user.get().getId(), jwtTokenDto.refreshToken());
            AlarmSetting alarmSetting = userAlarmSettingService.getUserAlarmSetting(fcmToken);

            boolean isPreferenceSettingCreated = userPreferenceSettingService
                    .readUserPreferenceSettingCreated(userId);
            UserPreferenceSettingDto userPreferenceSettingDto = userPreferenceSettingService.readUserPreference(
                    userId
            );

            UserNoticeResponseDto userNoticeResponseDto = userActivityService.getUserNotificationStatus(fcmToken);

            UserNotificationResponseDto userNotificationResponseDto = userActivityService.getUserNotificationActivity(
                    user.get(), fcmToken);
            PopupActivityResponseDto popupActivityResponseDto = userActivityService.getPopupActivity(user.get());

            UserActivityResponseDto userActivities = UserActivityResponseDto.fromProperties(
                    popupActivityResponseDto,
                    userNotificationResponseDto
            );

            List<String> blockedPopups = blockPopupService.findBlockedPopupList(user.get());
            List<String> blockedUsers = blockUserService.findBlockedUserList(user.get());

            UserRelationDto userRelationDto = UserRelationDto.ofBlockedUserIdsAndPopupIds(blockedUsers, blockedPopups);

            return UserInfoResponseDto.fromUserEntity(
                    user.get(),
                    alarmSetting,
                    jwtTokenDto,
                    userPreferenceSettingDto,
                    userNoticeResponseDto,
                    userActivities,
                    userRelationDto,
                    isPreferenceSettingCreated
            );
        } else {
            // 비밀번호 랜덤 생성 후 암호화해서 DB에 저장
            User newUser = userQueryRepository.findByEmail(oAuth2UserInfo.email())
                    .orElseGet(() -> userQueryRepository.save(
                            User.toGuestEntity(oAuth2UserInfo,
                                    bCryptPasswordEncoder.encode(PasswordUtil.generateRandomPassword()),
                                    provider))
                    );
            // 유저에게 GUEST 권한 주기
            JwtTokenDto jwtTokenDto = jwtUtil.generateToken(newUser.getId(), EUserRole.GUEST);
            String accessToken = jwtTokenDto.accessToken();
            userCommandRepository.updateRefreshToken(newUser.getId(), jwtTokenDto.refreshToken());
            return new AccessTokenDto(accessToken);
        }
    }

    // Header에 BASIC으로 로그인
    public UserInfoResponseDto authSignIn(String authorizationHeader, FcmTokenRequestDto fcmTokenRequestDto) {
        String encoded = HeaderUtil.refineHeader(authorizationHeader, Constant.BASIC_PREFIX);
        String[] decoded = new String(Base64.getDecoder().decode(encoded)).split(":");
        String email = decoded[0];
        String password = decoded[1];
        String fcmToken = fcmTokenRequestDto.fcmToken();

        User user = userQueryUseCase.findUserByEmail(email);
        Long userId = user.getId();

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new CommonException(ErrorCode.INVALID_LOGIN);
        }

        AlarmSetting alarmSetting = userAlarmSettingService.getUserAlarmSetting(fcmToken);

        // FCM 토큰 검증
        tokenCommandUseCase.refreshToken(userId, fcmToken);

        JwtTokenDto jwtTokenDto = jwtUtil.generateToken(userId, user.getRole());
        userCommandRepository.updateRefreshToken(userId, jwtTokenDto.refreshToken());

        // 유저 팝업 취향 설정 반환
        boolean isPreferenceSettingCreated = userPreferenceSettingService
                .readUserPreferenceSettingCreated(userId);
        UserPreferenceSettingDto userPreferenceSettingDto = userPreferenceSettingService
                .readUserPreference(userId);

        UserNoticeResponseDto userNoticeResponseDto = userActivityService.getUserNotificationStatus(fcmToken);

        UserNotificationResponseDto userNotificationResponseDto = userActivityService
                .getUserNotificationActivity(user, fcmToken);

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

    // Body에 email, password 받는 버전
    public UserInfoResponseDto authLogin(AuthLoginRequestDto authLoginRequestDto) {
        String email = authLoginRequestDto.email();
        String password = authLoginRequestDto.password();
        String fcmToken = authLoginRequestDto.fcmToken();

        User user = userQueryUseCase.findUserByEmail(email);
        Long userId = user.getId();

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new CommonException(ErrorCode.INVALID_LOGIN);
        }

        AlarmSetting alarmSetting = userAlarmSettingService.getUserAlarmSetting(fcmToken);

        // FCM 토큰 검증
        tokenCommandUseCase.refreshToken(userId, fcmToken);

        JwtTokenDto jwtTokenDto = jwtUtil.generateToken(userId, user.getRole());
        userCommandRepository.updateRefreshToken(userId, jwtTokenDto.refreshToken());

        boolean isPreferenceSettingCreated = userPreferenceSettingService
                .readUserPreferenceSettingCreated(userId);
        UserPreferenceSettingDto userPreferenceSettingDto = userPreferenceSettingService
                .readUserPreference(userId);

        UserNoticeResponseDto userNoticeResponseDto = userActivityService.getUserNotificationStatus(fcmToken);

        UserNotificationResponseDto userNotificationResponseDto = userActivityService.getUserNotificationActivity(user,
                fcmToken);
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

    private UserInfoResponseDto buildUserInfoResponse() {
        return null;
    }
}
