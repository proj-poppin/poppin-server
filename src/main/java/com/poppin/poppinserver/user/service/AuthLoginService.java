package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import com.poppin.poppinserver.alarm.domain.InformAlarm;
import com.poppin.poppinserver.alarm.domain.InformIsRead;
import com.poppin.poppinserver.alarm.domain.PopupAlarm;
import com.poppin.poppinserver.alarm.domain.type.ENotificationCategory;
import com.poppin.poppinserver.alarm.dto.DestinationResponseDto;
import com.poppin.poppinserver.alarm.dto.NotificationResponseDto;
import com.poppin.poppinserver.alarm.repository.InformIsReadRepository;
import com.poppin.poppinserver.alarm.repository.PopupAlarmRepository;
import com.poppin.poppinserver.alarm.usecase.TokenCommandUseCase;
import com.poppin.poppinserver.core.constant.Constant;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.util.HeaderUtil;
import com.poppin.poppinserver.core.util.JwtUtil;
import com.poppin.poppinserver.core.util.OAuth2Util;
import com.poppin.poppinserver.core.util.PasswordUtil;
import com.poppin.poppinserver.interest.domain.Interest;
import com.poppin.poppinserver.interest.repository.InterestRepository;
import com.poppin.poppinserver.popup.domain.Waiting;
import com.poppin.poppinserver.popup.dto.popup.response.PopupActivityResponseDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupScrapDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupWaitingDto;
import com.poppin.poppinserver.popup.repository.BlockedPopupRepository;
import com.poppin.poppinserver.popup.repository.WaitingRepository;
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
import com.poppin.poppinserver.user.repository.BlockedUserQueryRepository;
import com.poppin.poppinserver.user.repository.UserCommandRepository;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import com.poppin.poppinserver.visit.domain.Visit;
import com.poppin.poppinserver.visit.dto.visit.response.VisitDto;
import com.poppin.poppinserver.visit.repository.VisitRepository;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthLoginService {
    private final OAuth2Util oAuth2Util;
    private final UserQueryUseCase userQueryUseCase;
    private final UserCommandRepository userCommandRepository;
    private final UserAlarmSettingService userAlarmSettingService;
    private final UserPreferenceSettingService userPreferenceSettingService;
    private final InformIsReadRepository informIsReadRepository;
    private final PopupAlarmRepository popupAlarmRepository;
    private final UserQueryRepository userQueryRepository;
    private final JwtUtil jwtUtil;
    private final BlockedPopupRepository blockedPopupRepository;
    private final BlockedUserQueryRepository blockedUserQueryRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final InterestRepository interestRepository;
    private final AuthService authService;
    private final TokenCommandUseCase tokenCommandUseCase;
    private final VisitRepository visitRepository;
    private final WaitingRepository waitingRepository;

    @Transactional
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
        User user = userQueryRepository.findByEmail(appleUserIdRequestDto.appleUserId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

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

        Optional<User> user = userQueryRepository.findByEmailAndRole(oAuth2UserInfo.email(), EUserRole.USER);
        // 회원 탈퇴 여부 확인
        if (user.isPresent() && user.get().getIsDeleted()) {
            throw new CommonException(ErrorCode.DELETED_USER_ERROR);

        }

        // 이미 가입된 계정이 있는지 확인
        if (user.isPresent() && !user.get().getProvider().equals(provider)) {
            throw new CommonException(ErrorCode.DUPLICATED_SOCIAL_ID);
        }

        // USER 권한 + 이메일 정보가 DB에 존재 -> 팝핀 토큰 발급 및 로그인 상태 변경
        if (user.isPresent()) {
            Long userId = user.get().getId();
            JwtTokenDto jwtTokenDto = jwtUtil.generateToken(user.get().getId(), EUserRole.USER);
            userCommandRepository.updateRefreshTokenAndLoginStatus(user.get().getId(), jwtTokenDto.refreshToken(),
                    true);
            AlarmSetting alarmSetting = userAlarmSettingService.getUserAlarmSetting(fcmToken);
            boolean isPreferenceSettingCreated = userPreferenceSettingService
                    .readUserPreferenceSettingCreated(userId);
            UserPreferenceSettingDto userPreferenceSettingDto = userPreferenceSettingService.readUserPreference(
                    userId
            );
            // 유저가 읽은 공지사항 알람 리스트 조회
            List<String> checkedNoticeIds = informIsReadRepository.findReadInformAlarmIdsByFcmToken(
                    fcmToken).stream().map(
                    Object::toString
            ).toList();

            // 유저가 가장 최근에 읽은 공지사항 알람 시간 조회
            String informLastCheckedTime = informIsReadRepository.findLastReadTimeByFcmToken(
                    fcmToken);

            UserNoticeResponseDto userNoticeResponseDto = UserNoticeResponseDto.builder()
                    .lastCheck(informLastCheckedTime)
                    .checkedNoticeIds(checkedNoticeIds)
                    .build();

            // TODO: 여기부터 수정 필요
            DestinationResponseDto destinationResponseDto = DestinationResponseDto.fromProperties(
                    null, null, null, null,
                    null, null, null, null, null
            );

            List<PopupAlarm> userPopupAlarm = popupAlarmRepository.findByFcmToken(fcmToken);
            List<InformIsRead> userInformIsRead = informIsReadRepository.findAllByFcmToken(fcmToken);

            List<NotificationResponseDto> popupNotificationResponseDtoList = userPopupAlarm.stream().map(
                    popupAlarm -> NotificationResponseDto.fromProperties(
                            String.valueOf(popupAlarm.getId()), String.valueOf(user.get().getId()), null,
                            String.valueOf(ENotificationCategory.POPUP),
                            popupAlarm.getTitle(), popupAlarm.getBody(), null, popupAlarm.getIsRead(),
                            String.valueOf(popupAlarm.getCreatedAt()), String.valueOf(popupAlarm.getPopupId()), null,
                            destinationResponseDto
                    )
            ).toList();

            List<NotificationResponseDto> noticeNotificationResponseDtoList = userInformIsRead.stream()
                    .map(informIsRead -> {
                        InformAlarm informAlarm = informIsRead.getInformAlarm();
                        Boolean isRead = informIsRead.getIsRead();

                        return NotificationResponseDto.fromProperties(
                                String.valueOf(informAlarm.getId()),
                                String.valueOf(user.get().getId()),
                                null,
                                String.valueOf(ENotificationCategory.NOTICE),
                                informAlarm.getTitle(),
                                informAlarm.getBody(),
                                null,
                                isRead,
                                String.valueOf(informAlarm.getCreatedAt()),
                                null,
                                String.valueOf(informAlarm.getId()),
                                destinationResponseDto
                        );
                    }).toList();

            UserNotificationResponseDto userNotificationResponseDto = UserNotificationResponseDto.fromDtoList(
                    popupNotificationResponseDtoList,
                    noticeNotificationResponseDtoList
            );

            List<Interest> userInterestPopupList = interestRepository.findByUserId(user.get().getId());

            List<PopupScrapDto> popupScrapDtoList = userInterestPopupList.stream().map(
                    PopupScrapDto::fromInterest
            ).toList();

            List<Visit> userVisitedPopupList = visitRepository.findAllByUserId(userId);

            List<VisitDto> userVisitedPopupDtoList = userVisitedPopupList.stream()
                    .map(
                            v -> VisitDto.fromEntity(
                                    v.getId(), v.getPopup().getId(), v.getUser().getId(), v.getCreatedAt().toString()
                            )
                    ).toList();

            List<Waiting> userWaitingPopupList = waitingRepository.findAllByUserId(userId);
            List<PopupWaitingDto> userWaitingPopupDtoList = userWaitingPopupList.stream()
                    .map(
                            pw -> PopupWaitingDto.fromEntity(
                                    pw.getId(), pw.getPopup().getId()
                            )
                    ).toList();

            PopupActivityResponseDto popupActivityResponseDto = PopupActivityResponseDto
                    .fromProperties(popupScrapDtoList, userVisitedPopupDtoList, userWaitingPopupDtoList);

            UserActivityResponseDto userActivities = UserActivityResponseDto.fromProperties(
                    popupActivityResponseDto,
                    userNotificationResponseDto
            );

            List<String> blockedPopups = blockedPopupRepository.findAllByUserId(user.get()).stream()
                    .map(blockedPopup -> blockedPopup.getId().toString())
                    .toList();
            List<String> blockedUsers = blockedUserQueryRepository.findAllByUserId(user.get()).stream()
                    .map(blockedUser -> blockedUser.getId().toString())
                    .toList();
            UserRelationDto userRelationDto = UserRelationDto.ofBlockedUserIdsAndPopupIds(blockedUsers, blockedPopups);

            // TODO: 여기까지 수정 필요

            UserInfoResponseDto userInfoResponseDto = UserInfoResponseDto.fromUserEntity(
                    user.get(),
                    alarmSetting,
                    jwtTokenDto,
                    userPreferenceSettingDto,
                    userNoticeResponseDto,
                    userActivities,
                    userRelationDto,
                    isPreferenceSettingCreated
            );
            return userInfoResponseDto;
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
            userCommandRepository.updateRefreshTokenAndLoginStatus(newUser.getId(), jwtTokenDto.refreshToken(), true);
            return new AccessTokenDto(accessToken);
        }
    }

    // 헤더에 BASIC으로 로그인
    @Transactional
    public UserInfoResponseDto authSignIn(String authorizationHeader, FcmTokenRequestDto fcmTokenRequestDto) {
        String encoded = HeaderUtil.refineHeader(authorizationHeader, Constant.BASIC_PREFIX);
        String[] decoded = new String(Base64.getDecoder().decode(encoded)).split(":");
        String email = decoded[0];
        String password = decoded[1];
        String fcmToken = fcmTokenRequestDto.fcmToken();

        User user = userQueryRepository.findByEmail(email)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new CommonException(ErrorCode.INVALID_LOGIN);
        }

        AlarmSetting alarmSetting = userAlarmSettingService.getUserAlarmSetting(fcmToken);

        // FCM 토큰 검증
        tokenCommandUseCase.refreshToken(user.getId(), fcmToken);

        JwtTokenDto jwtTokenDto = jwtUtil.generateToken(user.getId(), user.getRole());
        userCommandRepository.updateRefreshTokenAndLoginStatus(user.getId(), jwtTokenDto.refreshToken(), true);

        boolean isPreferenceSettingCreated = userPreferenceSettingService
                .readUserPreferenceSettingCreated(user.getId());
        UserPreferenceSettingDto userPreferenceSettingDto = userPreferenceSettingService.readUserPreference(
                user.getId()
        );

        // 유저가 읽은 공지사항 알람 리스트 조회
        List<String> checkedNoticeIds = informIsReadRepository.findReadInformAlarmIdsByFcmToken(
                fcmToken).stream().map(
                Object::toString
        ).toList();

        // 유저가 가장 최근에 읽은 공지사항 알람 시간 조회
        String informLastCheckedTime = informIsReadRepository.findLastReadTimeByFcmToken(
                fcmToken);

        UserNoticeResponseDto userNoticeResponseDto = UserNoticeResponseDto.builder()
                .lastCheck(informLastCheckedTime)
                .checkedNoticeIds(checkedNoticeIds)
                .build();

        // TODO: 여기부터 수정 필요
        DestinationResponseDto destinationResponseDto = DestinationResponseDto.fromProperties(
                null, null, null, null,
                null, null, null, null, null
        );

        List<PopupAlarm> userPopupAlarm = popupAlarmRepository.findByFcmToken(fcmToken);
        List<InformIsRead> userInformIsRead = informIsReadRepository.findAllByFcmToken(fcmToken);

        List<NotificationResponseDto> popupNotificationResponseDtoList = userPopupAlarm.stream().map(
                popupAlarm -> NotificationResponseDto.fromProperties(
                        String.valueOf(popupAlarm.getId()), String.valueOf(user.getId()), null,
                        String.valueOf(ENotificationCategory.POPUP),
                        popupAlarm.getTitle(), popupAlarm.getBody(), null, popupAlarm.getIsRead(),
                        String.valueOf(popupAlarm.getCreatedAt()), String.valueOf(popupAlarm.getPopupId()), null,
                        destinationResponseDto
                )
        ).toList();

        List<NotificationResponseDto> noticeNotificationResponseDtoList = userInformIsRead.stream()
                .map(informIsRead -> {
                    InformAlarm informAlarm = informIsRead.getInformAlarm();
                    Boolean isRead = informIsRead.getIsRead();

                    return NotificationResponseDto.fromProperties(
                            String.valueOf(informAlarm.getId()),
                            String.valueOf(user.getId()),
                            null,
                            String.valueOf(ENotificationCategory.NOTICE),
                            informAlarm.getTitle(),
                            informAlarm.getBody(),
                            null,
                            isRead,
                            String.valueOf(informAlarm.getCreatedAt()),
                            null,
                            String.valueOf(informAlarm.getId()),
                            destinationResponseDto
                    );
                }).toList();

        UserNotificationResponseDto userNotificationResponseDto = UserNotificationResponseDto.fromDtoList(
                popupNotificationResponseDtoList,
                noticeNotificationResponseDtoList
        );

        List<Interest> userInterestPopupList = interestRepository.findByUserId(user.getId());

        List<PopupScrapDto> popupScrapDtoList = userInterestPopupList.stream().map(
                PopupScrapDto::fromInterest
        ).toList();

        List<Visit> userVisitedPopupList = visitRepository.findAllByUserId(user.getId());

        List<VisitDto> userVisitedPopupDtoList = userVisitedPopupList.stream()
                .map(
                        v -> VisitDto.fromEntity(
                                v.getId(), v.getPopup().getId(), v.getUser().getId(), v.getCreatedAt().toString()
                        )
                ).toList();

        List<Waiting> userWaitingPopupList = waitingRepository.findAllByUserId(user.getId());
        List<PopupWaitingDto> userWaitingPopupDtoList = userWaitingPopupList.stream()
                .map(
                        pw -> PopupWaitingDto.fromEntity(
                                pw.getId(), pw.getPopup().getId()
                        )
                ).toList();

        PopupActivityResponseDto popupActivityResponseDto = PopupActivityResponseDto
                .fromProperties(popupScrapDtoList, userVisitedPopupDtoList, userWaitingPopupDtoList);

        UserActivityResponseDto userActivities = UserActivityResponseDto.fromProperties(
                popupActivityResponseDto,
                userNotificationResponseDto
        );

        List<String> blockedPopups = blockedPopupRepository.findAllByUserId(user).stream()
                .map(blockedPopup -> blockedPopup.getId().toString())
                .toList();
        List<String> blockedUsers = blockedUserQueryRepository.findAllByUserId(user).stream()
                .map(blockedUser -> blockedUser.getId().toString())
                .toList();
        UserRelationDto userRelationDto = UserRelationDto.ofBlockedUserIdsAndPopupIds(blockedUsers, blockedPopups);

        // TODO: 여기까지 수정 필요

        UserInfoResponseDto userInfoResponseDto = UserInfoResponseDto.fromUserEntity(
                user,
                alarmSetting,
                jwtTokenDto,
                userPreferenceSettingDto,
                userNoticeResponseDto,
                userActivities,
                userRelationDto,
                isPreferenceSettingCreated
        );

        return userInfoResponseDto;
    }

    // Body에 email, password 받는 버전
    @Transactional
    public UserInfoResponseDto authLogin(AuthLoginRequestDto authLoginRequestDto) {
        String email = authLoginRequestDto.email();
        String password = authLoginRequestDto.password();
        String fcmToken = authLoginRequestDto.fcmToken();

        User user = userQueryRepository.findByEmail(email)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new CommonException(ErrorCode.INVALID_LOGIN);
        }

        AlarmSetting alarmSetting = userAlarmSettingService.getUserAlarmSetting(fcmToken);

        // FCM 토큰 검증
        tokenCommandUseCase.refreshToken(user.getId(), fcmToken);

        JwtTokenDto jwtTokenDto = jwtUtil.generateToken(user.getId(), user.getRole());
        userCommandRepository.updateRefreshTokenAndLoginStatus(user.getId(), jwtTokenDto.refreshToken(), true);

        boolean isPreferenceSettingCreated = userPreferenceSettingService
                .readUserPreferenceSettingCreated(user.getId());
        UserPreferenceSettingDto userPreferenceSettingDto = userPreferenceSettingService.readUserPreference(
                user.getId()
        );

        // 유저가 읽은 공지사항 알람 리스트 조회
        List<String> checkedNoticeIds = informIsReadRepository.findReadInformAlarmIdsByFcmToken(
                fcmToken).stream().map(
                Object::toString
        ).toList();

        // 유저가 가장 최근에 읽은 공지사항 알람 시간 조회
        String informLastCheckedTime = informIsReadRepository.findLastReadTimeByFcmToken(
                fcmToken);

        UserNoticeResponseDto userNoticeResponseDto = UserNoticeResponseDto.builder()
                .lastCheck(informLastCheckedTime)
                .checkedNoticeIds(checkedNoticeIds)
                .build();

        // TODO: 여기부터 수정 필요
        DestinationResponseDto destinationResponseDto = DestinationResponseDto.fromProperties(
                null, null, null, null,
                null, null, null, null, null
        );

        List<PopupAlarm> userPopupAlarm = popupAlarmRepository.findByFcmToken(fcmToken);
        List<InformIsRead> userInformIsRead = informIsReadRepository.findAllByFcmToken(fcmToken);

        List<NotificationResponseDto> popupNotificationResponseDtoList = userPopupAlarm.stream().map(
                popupAlarm -> NotificationResponseDto.fromProperties(
                        String.valueOf(popupAlarm.getId()), String.valueOf(user.getId()), null,
                        String.valueOf(ENotificationCategory.POPUP),
                        popupAlarm.getTitle(), popupAlarm.getBody(), null, popupAlarm.getIsRead(),
                        String.valueOf(popupAlarm.getCreatedAt()), String.valueOf(popupAlarm.getPopupId()), null,
                        destinationResponseDto
                )
        ).toList();

        List<NotificationResponseDto> noticeNotificationResponseDtoList = userInformIsRead.stream()
                .map(informIsRead -> {
                    InformAlarm informAlarm = informIsRead.getInformAlarm();
                    Boolean isRead = informIsRead.getIsRead();

                    return NotificationResponseDto.fromProperties(
                            String.valueOf(informAlarm.getId()),
                            String.valueOf(user.getId()),
                            null,
                            String.valueOf(ENotificationCategory.NOTICE),
                            informAlarm.getTitle(),
                            informAlarm.getBody(),
                            null,
                            isRead,
                            String.valueOf(informAlarm.getCreatedAt()),
                            null,
                            String.valueOf(informAlarm.getId()),
                            destinationResponseDto
                    );
                }).toList();

        UserNotificationResponseDto userNotificationResponseDto = UserNotificationResponseDto.fromDtoList(
                popupNotificationResponseDtoList,
                noticeNotificationResponseDtoList
        );

        List<Interest> userInterestPopupList = interestRepository.findByUserId(user.getId());

        List<PopupScrapDto> popupScrapDtoList = userInterestPopupList.stream().map(
                PopupScrapDto::fromInterest
        ).toList();

        List<Visit> userVisitedPopupList = visitRepository.findAllByUserId(user.getId());

        List<VisitDto> userVisitedPopupDtoList = userVisitedPopupList.stream()
                .map(
                        v -> VisitDto.fromEntity(
                                v.getId(), v.getPopup().getId(), v.getUser().getId(), v.getCreatedAt().toString()
                        )
                ).toList();

        List<Waiting> userWaitingPopupList = waitingRepository.findAllByUserId(user.getId());
        List<PopupWaitingDto> userWaitingPopupDtoList = userWaitingPopupList.stream()
                .map(
                        pw -> PopupWaitingDto.fromEntity(
                                pw.getId(), pw.getPopup().getId()
                        )
                ).toList();

        PopupActivityResponseDto popupActivityResponseDto = PopupActivityResponseDto
                .fromProperties(popupScrapDtoList, userVisitedPopupDtoList, userWaitingPopupDtoList);

        UserActivityResponseDto userActivities = UserActivityResponseDto.fromProperties(
                popupActivityResponseDto,
                userNotificationResponseDto
        );

        List<String> blockedPopups = blockedPopupRepository.findAllByUserId(user).stream()
                .map(blockedPopup -> blockedPopup.getId().toString())
                .toList();
        List<String> blockedUsers = blockedUserQueryRepository.findAllByUserId(user).stream()
                .map(blockedUser -> blockedUser.getId().toString())
                .toList();
        UserRelationDto userRelationDto = UserRelationDto.ofBlockedUserIdsAndPopupIds(blockedUsers, blockedPopups);

        // TODO: 여기까지 수정 필요

        UserInfoResponseDto userInfoResponseDto = UserInfoResponseDto.fromUserEntity(
                user,
                alarmSetting,
                jwtTokenDto,
                userPreferenceSettingDto,
                userNoticeResponseDto,
                userActivities,
                userRelationDto,
                isPreferenceSettingCreated
        );

        return userInfoResponseDto;
    }
}
