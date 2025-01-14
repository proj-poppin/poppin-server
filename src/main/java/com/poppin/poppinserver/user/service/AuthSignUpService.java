package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import com.poppin.poppinserver.alarm.usecase.TokenCommandUseCase;
import com.poppin.poppinserver.core.util.JwtUtil;
import com.poppin.poppinserver.popup.dto.popup.response.PopupActivityResponseDto;
import com.poppin.poppinserver.popup.service.BlockedPopupService;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.domain.type.EUserRole;
import com.poppin.poppinserver.user.dto.auth.request.AuthSignUpRequestDto;
import com.poppin.poppinserver.user.dto.auth.response.JwtTokenDto;
import com.poppin.poppinserver.user.dto.user.response.UserActivityResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserInfoResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserNoticeResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserNotificationResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserPreferenceSettingDto;
import com.poppin.poppinserver.user.dto.user.response.UserRelationDto;
import com.poppin.poppinserver.user.usecase.UserCommandUseCase;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 소셜 회원가입과 일반 회원가입을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthSignUpService {
    private final UserQueryUseCase userQueryUseCase;
    private final UserCommandUseCase userCommandUseCase;

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

    public UserInfoResponseDto handleSignUp(AuthSignUpRequestDto authSignUpRequestDto) {
        return Optional.ofNullable(authSignUpRequestDto.password())
                .filter(password -> !password.isEmpty())
                .map(password -> defaultSignUp(authSignUpRequestDto)) // 자체 회원가입 로직 처리
                .orElseGet(() -> socialSignUp(authSignUpRequestDto)); // 소셜 회원가입 로직 처리
    }

    // 자체 회원가입
    private UserInfoResponseDto defaultSignUp(AuthSignUpRequestDto authSignUpRequestDto) {
        String email = authSignUpRequestDto.email();
        String password = authSignUpRequestDto.password();
        String passwordConfirm = authSignUpRequestDto.passwordConfirm();
        String nickname = authSignUpRequestDto.nickname();

        // 유저 이메일 중복 확인
        userQueryUseCase.checkDuplicatedEmail(email);

        // 비밀번호와 비밀번호 확인 일치 여부 검증
        userQueryUseCase.checkPasswordMatch(password, passwordConfirm);

        // 유저 닉네임 중복 확인
        userQueryUseCase.checkDuplicatedNickname(nickname);

        // 유저 생성, 패스워드 암호화
        User newUser = userCommandUseCase.createUserByDefaultSignUp(authSignUpRequestDto);

        return buildUserInfoResponse(authSignUpRequestDto, newUser);
    }

    // 소셜 회원가입
    private UserInfoResponseDto socialSignUp(AuthSignUpRequestDto authSignUpRequestDto) {
        String email = authSignUpRequestDto.email();
        String nickname = authSignUpRequestDto.nickname();

        // 유저 이메일 중복 확인
        userQueryUseCase.checkDuplicatedEmail(email);

        // 유저 닉네임 중복 확인
        userQueryUseCase.checkDuplicatedNickname(nickname);

        // 유저 생성, 패스워드 암호화
        User newUser = userCommandUseCase.createUserBySocialSignUp(authSignUpRequestDto);

        return buildUserInfoResponse(authSignUpRequestDto, newUser);
    }

    private UserInfoResponseDto buildUserInfoResponse(AuthSignUpRequestDto authSignUpRequestDto, User newUser) {
        String fcmToken = authSignUpRequestDto.fcmToken();

        // 알람 setting 객체 반환
        AlarmSetting alarmSetting = userAlarmSettingService.getUserAlarmSetting(fcmToken);

        // FCM 토큰 등록
        tokenCommandUseCase.applyToken(fcmToken, newUser.getId());

        // 회원 가입 후 바로 로그인 상태로 변경
        JwtTokenDto jwtToken = jwtUtil.generateToken(newUser.getId(), EUserRole.USER);

        // 리프레시 토큰 업데이트
        newUser.updateRefreshToken(jwtToken.refreshToken());

        // 유저 취향 설정 정보 조회
        boolean isPreferenceSettingCreated = userPreferenceSettingService
                .readUserPreferenceSettingCreated(newUser.getId());
        UserPreferenceSettingDto userPreferenceSettingDto = userPreferenceSettingService.readUserPreference(
                newUser.getId()
        );

        // 유저 공지사항 알림 정보 조회
        UserNoticeResponseDto userNoticeResponseDto = userActivityService.getUserNotificationStatus(fcmToken);

        // 유저 알림 정보 조회
        UserNotificationResponseDto userNotificationResponseDto = userActivityService.getUserNotificationActivity(
                newUser, fcmToken
        );

        // 유저 팝업 정보 조회
        PopupActivityResponseDto popupActivityResponseDto = userActivityService.getPopupActivity(newUser);

        // 유저 활동 정보 조회
        UserActivityResponseDto userActivities = UserActivityResponseDto.fromProperties(
                popupActivityResponseDto,
                userNotificationResponseDto
        );

        // 차단 정보 조회
        List<String> blockedPopups = blockPopupService.findBlockedPopupList(newUser);
        List<String> blockedUsers = blockUserService.findBlockedUserList(newUser);

        UserRelationDto userRelationDto = UserRelationDto.ofBlockedUserIdsAndPopupIds(blockedUsers, blockedPopups);

        return UserInfoResponseDto.fromUserEntity(
                newUser,
                alarmSetting,
                jwtToken,
                userPreferenceSettingDto,
                userNoticeResponseDto,
                userActivities,
                userRelationDto,
                isPreferenceSettingCreated
        );
    }
}
