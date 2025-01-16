package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import com.poppin.poppinserver.alarm.repository.PopupAlarmRepository;
import com.poppin.poppinserver.alarm.repository.UserInformAlarmRepository;
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
import com.poppin.poppinserver.user.dto.auth.request.*;
import com.poppin.poppinserver.user.dto.auth.response.AccountStatusResponseDto;
import com.poppin.poppinserver.user.dto.auth.response.AuthCodeResponseDto;
import com.poppin.poppinserver.user.dto.auth.response.JwtTokenDto;
import com.poppin.poppinserver.user.dto.user.response.*;
import com.poppin.poppinserver.user.repository.UserCommandRepository;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserQueryRepository userQueryRepository;
    private final JwtUtil jwtUtil;
    private final MailService mailService;
    private final UserAlarmSettingService userAlarmSettingService;
    private final UserPreferenceSettingService userPreferenceSettingService;
    private final UserInformAlarmRepository userInformAlarmRepository;
    private final PopupAlarmRepository popupAlarmRepository;
    private final UserCommandRepository userCommandRepository;
    private final UserQueryUseCase userQueryUseCase;
    private final TokenCommandUseCase tokenCommandUseCase;

    // 차단
    private final BlockUserService blockUserService;
    private final BlockedPopupService blockPopupService;

    // 유저의 활동 내역
    private final UserActivityService userActivityService;

//    @Transactional
//    public UserInfoResponseDto socialSignUp(String accessToken,
//                                            SocialRegisterRequestDto socialRegisterRequestDto) {  // 소셜 로그인 후 회원 등록 및 토큰 발급
//        String token = refineToken(accessToken);    // poppin access token
//
//        Long userId = jwtUtil.getUserIdFromToken(token);    // 토큰으로부터 id 추출
//
//        // 소셜 회원가입 시, id와 provider로 유저 정보를 찾음
//        User user = userRepository.findByIdAndELoginProvider(userId,
//                        ELoginProvider.valueOf(socialRegisterRequestDto.provider()))
//                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
//
//        // 닉네임 등록 -> 소셜 회원가입 완료
//        user.register(socialRegisterRequestDto.nickname());
//
//        final JwtTokenDto jwtTokenDto = jwtUtil.generateToken(user.getId(), user.getRole());
//        user.updateRefreshToken(jwtTokenDto.refreshToken());
//        AlarmSetting alarmSetting = userAlarmSettingService.getUserAlarmSetting(socialRegisterRequestDto.fcmToken());
//
//        UserPreferenceSettingDto userPreferenceSettingDto = userPreferenceSettingService.readUserPreferenceSettingCreated(
//                user.getId()
//        );
//
//        UserInfoResponseDto userInfoResponseDto = UserInfoResponseDto.fromUserEntity(
//                user,
//                alarmSetting,
//                jwtTokenDto,
//                userPreferenceSettingDto
//        );
//        return userInfoResponseDto;
//    }

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
        boolean userExists = userQueryRepository.findByEmail(email).isPresent();

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
        AlarmSetting alarmSetting = userAlarmSettingService.getUserAlarmSetting(fcmToken);

        // FCM 토큰 검증
        tokenCommandUseCase.refreshToken(user.getId(), fcmToken);
        userCommandRepository.updateRefreshToken(user.getId(), jwtTokenDto.refreshToken());

        boolean isPreferenceSettingCreated = userPreferenceSettingService
                .readUserPreferenceSettingCreated(user.getId());
        UserPreferenceSettingDto userPreferenceSettingDto = userPreferenceSettingService.readUserPreference(
                user.getId()
        );

        // 유저가 읽은 공지사항+팝업 알림 리스트 조회
        Long checkedPopupIds = popupAlarmRepository.readPopupAlarms(userId);
        List<String> checkedNoticeIds = userInformAlarmRepository.findReadInformAlarmIdsByUserId(userId)
                .stream()
                .map(Object::toString)
                .toList();

        if (checkedPopupIds != null) {
            checkedNoticeIds = new ArrayList<>(checkedNoticeIds);
            checkedNoticeIds.add(checkedPopupIds.toString());
        }

        // 유저가 가장 최근에 읽은 공지사항 알람 시간 조회
        LocalDateTime informLastCheckedTime = userInformAlarmRepository
                .findLastReadTimeByUser(userId);

        UserNoticeResponseDto userNoticeResponseDto = UserNoticeResponseDto
                .of(informLastCheckedTime, checkedNoticeIds);

        UserNotificationResponseDto userNotificationResponseDto = userActivityService.getUserNotificationActivity(
                user, fcmToken);
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
        Optional<User> user = userQueryRepository.findByEmail(accountRequestDto.email());
        EAccountStatus accountStatus;
        if (user.isPresent()) {
            accountStatus = EAccountStatus.LOGIN;
        } else {
            accountStatus = EAccountStatus.SIGNUP;
        }
        return AccountStatusResponseDto.fromEnum(accountStatus);
    }

    @Transactional(readOnly = true)
    public AccountStatusResponseDto getAppleAccountStatus(AppleUserIdRequestDto appleUserIdRequestDto) {
        Optional<User> user = userQueryRepository.findByEmail(appleUserIdRequestDto.appleUserId());
        EAccountStatus accountStatus;
        if (user.isPresent()) {
            accountStatus = EAccountStatus.LOGIN;
        } else {
            accountStatus = EAccountStatus.SIGNUP;
        }
        return AccountStatusResponseDto.fromEnum(accountStatus);
    }

    public String refineToken(String accessToken) {
        return accessToken.startsWith(Constant.BEARER_PREFIX)
                ? accessToken.substring(Constant.BEARER_PREFIX.length())
                : accessToken;
    }
}
