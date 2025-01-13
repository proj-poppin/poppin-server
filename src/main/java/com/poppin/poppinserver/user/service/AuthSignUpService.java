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
import com.poppin.poppinserver.core.util.JwtUtil;
import com.poppin.poppinserver.interest.domain.Interest;
import com.poppin.poppinserver.interest.repository.InterestRepository;
import com.poppin.poppinserver.popup.domain.Waiting;
import com.poppin.poppinserver.popup.dto.popup.response.PopupActivityResponseDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupScrapDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupWaitingDto;
import com.poppin.poppinserver.popup.repository.BlockedPopupRepository;
import com.poppin.poppinserver.popup.repository.WaitingRepository;
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
import com.poppin.poppinserver.user.repository.BlockedUserQueryRepository;
import com.poppin.poppinserver.user.usecase.UserCommandUseCase;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import com.poppin.poppinserver.visit.domain.Visit;
import com.poppin.poppinserver.visit.dto.visit.response.VisitDto;
import com.poppin.poppinserver.visit.repository.VisitRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthSignUpService {
    private final UserQueryUseCase userQueryUseCase;
    private final UserCommandUseCase userCommandUseCase;
    private final UserAlarmSettingService userAlarmSettingService;
    private final JwtUtil jwtUtil;
    private final UserPreferenceSettingService userPreferenceSettingService;
    private final InformIsReadRepository informIsReadRepository;
    private final PopupAlarmRepository popupAlarmRepository;
    private final InterestRepository interestRepository;
    private final BlockedPopupRepository blockedPopupRepository;
    private final BlockedUserQueryRepository blockedUserQueryRepository;
    private final TokenCommandUseCase tokenCommandUseCase;
    private final VisitRepository visitRepository;
    private final WaitingRepository waitingRepository;

    public UserInfoResponseDto handleSignUp(AuthSignUpRequestDto authSignUpRequestDto) {
//        if (authSignUpRequestDto.password() == null || authSignUpRequestDto.password().isEmpty()) {
//            // 소셜 로그인 로직 처리
//            return socialSignUp(authSignUpRequestDto);
//        } else {
//            // 자체 로그인 로직 처리
//            return defaultSignUp(authSignUpRequestDto);
//        }

        return Optional.ofNullable(authSignUpRequestDto.password())
                .filter(password -> !password.isEmpty())
                .map(password -> defaultSignUp(authSignUpRequestDto))
                .orElseGet(() -> socialSignUp(authSignUpRequestDto));
    }

    // 자체 회원가입
    private UserInfoResponseDto defaultSignUp(AuthSignUpRequestDto authSignUpRequestDto) {
        // 유저 이메일 중복 확인
        userQueryUseCase.checkDuplicatedEmail(authSignUpRequestDto.email());

        // 비밀번호와 비밀번호 확인 일치 여부 검증
        userQueryUseCase.checkPasswordMatch(authSignUpRequestDto.password(), authSignUpRequestDto.passwordConfirm());

        // 유저 닉네임 중복 확인
        userQueryUseCase.checkDuplicatedNickname(authSignUpRequestDto.nickname());

        // 유저 생성, 패스워드 암호화
        User newUser = userCommandUseCase.createUserByDefaultSignUp(authSignUpRequestDto);

        // 알람 setting 객체 반환
        AlarmSetting alarmSetting = userAlarmSettingService.getUserAlarmSetting(authSignUpRequestDto.fcmToken());

        // FCM 토큰 등록
        tokenCommandUseCase.applyToken(authSignUpRequestDto.fcmToken(), newUser.getId());

        // 회원 가입 후 바로 로그인 상태로 변경
        JwtTokenDto jwtToken = jwtUtil.generateToken(newUser.getId(), EUserRole.USER);

        // 리프레시 토큰 업데이트 및 로그인 상태 변경
        newUser.updateRefreshTokenAndLoginStatus(jwtToken.refreshToken());
        //userCommandUseCase.updateRefreshTokenAndLoginStatus(newUser.getId(), jwtToken.refreshToken(), true);

        boolean isPreferenceSettingCreated = userPreferenceSettingService
                .readUserPreferenceSettingCreated(newUser.getId());
        UserPreferenceSettingDto userPreferenceSettingDto = userPreferenceSettingService.readUserPreference(
                newUser.getId()
        );

        // 유저가 읽은 공지사항 알람 리스트 조회
        List<String> checkedNoticeIds = informIsReadRepository.findReadInformAlarmIdsByFcmToken(
                authSignUpRequestDto.fcmToken()).stream().map(
                Object::toString
        ).toList();

        // 유저가 가장 최근에 읽은 공지사항 알람 시간 조회
        String informLastCheckedTime = informIsReadRepository.findLastReadTimeByFcmToken(
                authSignUpRequestDto.fcmToken());

        UserNoticeResponseDto userNoticeResponseDto = UserNoticeResponseDto.builder()
                .lastCheck(informLastCheckedTime)
                .checkedNoticeIds(checkedNoticeIds)
                .build();

        // TODO: 여기부터 수정 필요
        DestinationResponseDto destinationResponseDto = DestinationResponseDto.fromProperties(
                null, null, null, null,
                null, null, null, null, null
        );

        List<PopupAlarm> userPopupAlarm = popupAlarmRepository.findByFcmToken(authSignUpRequestDto.fcmToken());
        List<InformIsRead> userInformIsRead = informIsReadRepository.findAllByFcmToken(authSignUpRequestDto.fcmToken());

        List<NotificationResponseDto> popupNotificationResponseDtoList = userPopupAlarm.stream().map(
                popupAlarm -> NotificationResponseDto.fromProperties(
                        String.valueOf(popupAlarm.getId()), String.valueOf(newUser.getId()), null,
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
                            String.valueOf(newUser.getId()),
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

        List<Interest> userInterestPopupList = interestRepository.findByUserId(newUser.getId());

        List<PopupScrapDto> popupScrapDtoList = userInterestPopupList.stream().map(
                PopupScrapDto::fromInterest
        ).toList();

        List<Visit> userVisitedPopupList = visitRepository.findAllByUserId(newUser.getId());

        List<VisitDto> userVisitedPopupDtoList = userVisitedPopupList.stream()
                .map(
                        v -> VisitDto.fromEntity(
                                v.getId(), v.getPopup().getId(), v.getUser().getId(), v.getCreatedAt().toString()
                        )
                ).toList();

        List<Waiting> userWaitingPopupList = waitingRepository.findAllByUserId(newUser.getId());
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

        List<String> blockedPopups = blockedPopupRepository.findAllByUserId(newUser).stream()
                .map(blockedPopup -> blockedPopup.getId().toString())
                .toList();
        List<String> blockedUsers = blockedUserQueryRepository.findAllByUserId(newUser).stream()
                .map(blockedUser -> blockedUser.getId().toString())
                .toList();
        UserRelationDto userRelationDto = UserRelationDto.ofBlockedUserIdsAndPopupIds(blockedUsers, blockedPopups);

        // TODO: 여기까지 수정 필요

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

    // 소셜 회원가입
    private UserInfoResponseDto socialSignUp(AuthSignUpRequestDto authSignUpRequestDto) {  // 소셜 로그인 후 회원 등록 및 토큰 발급
        // DTO에서 소셜 프로바이더 추출
        // ELoginProvider provider = ELoginProvider.valueOf(authSignUpRequestDto.accountType());

        // 유저 이메일 중복 확인
        userQueryUseCase.checkDuplicatedEmail(authSignUpRequestDto.email());

        // 유저 닉네임 중복 확인
        userQueryUseCase.checkDuplicatedNickname(authSignUpRequestDto.nickname());

        // 유저 생성, 패스워드 암호화
        User newUser = userCommandUseCase.createUserBySocialSignUp(authSignUpRequestDto);

        // 알람 setting 객체 반환
        AlarmSetting alarmSetting = userAlarmSettingService.getUserAlarmSetting(authSignUpRequestDto.fcmToken());

        // FCM 토큰 등록
        tokenCommandUseCase.applyToken(authSignUpRequestDto.fcmToken(), newUser.getId());

        // 회원 가입 후 바로 로그인 상태로 변경
        JwtTokenDto jwtToken = jwtUtil.generateToken(newUser.getId(), EUserRole.USER);
        // userCommandUseCase.updateRefreshTokenAndLoginStatus(newUser.getId(), jwtToken.refreshToken(), true);

        // 리프레시 토큰 업데이트 및 로그인 상태 변경
        newUser.updateRefreshTokenAndLoginStatus(jwtToken.refreshToken());

        boolean isPreferenceSettingCreated = userPreferenceSettingService
                .readUserPreferenceSettingCreated(newUser.getId());
        UserPreferenceSettingDto userPreferenceSettingDto = userPreferenceSettingService.readUserPreference(
                newUser.getId()
        );

        // 유저가 읽은 공지사항 알람 리스트 조회
        List<String> checkedNoticeIds = informIsReadRepository.findReadInformAlarmIdsByFcmToken(
                authSignUpRequestDto.fcmToken()).stream().map(
                Object::toString
        ).toList();

        // 유저가 가장 최근에 읽은 공지사항 알람 시간 조회
        String informLastCheckedTime = informIsReadRepository.findLastReadTimeByFcmToken(
                authSignUpRequestDto.fcmToken());

        UserNoticeResponseDto userNoticeResponseDto = UserNoticeResponseDto.builder()
                .lastCheck(informLastCheckedTime)
                .checkedNoticeIds(checkedNoticeIds)
                .build();

        // TODO: 여기부터 수정 필요
        DestinationResponseDto destinationResponseDto = DestinationResponseDto.fromProperties(
                null, null, null, null,
                null, null, null, null, null
        );

        List<PopupAlarm> userPopupAlarm = popupAlarmRepository.findByFcmToken(authSignUpRequestDto.fcmToken());
        List<InformIsRead> userInformIsRead = informIsReadRepository.findAllByFcmToken(authSignUpRequestDto.fcmToken());

        List<NotificationResponseDto> popupNotificationResponseDtoList = userPopupAlarm.stream().map(
                popupAlarm -> NotificationResponseDto.fromProperties(
                        String.valueOf(popupAlarm.getId()), String.valueOf(newUser.getId()), null,
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
                            String.valueOf(newUser.getId()),
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

        List<Interest> userInterestPopupList = interestRepository.findByUserId(newUser.getId());

        List<PopupScrapDto> popupScrapDtoList = userInterestPopupList.stream().map(
                PopupScrapDto::fromInterest
        ).toList();

        List<Visit> userVisitedPopupList = visitRepository.findAllByUserId(newUser.getId());

        List<VisitDto> userVisitedPopupDtoList = userVisitedPopupList.stream()
                .map(
                        v -> VisitDto.fromEntity(
                                v.getId(), v.getPopup().getId(), v.getUser().getId(), v.getCreatedAt().toString()
                        )
                ).toList();

        List<Waiting> userWaitingPopupList = waitingRepository.findAllByUserId(newUser.getId());
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

        List<String> blockedPopups = blockedPopupRepository.findAllByUserId(newUser).stream()
                .map(blockedPopup -> blockedPopup.getId().toString())
                .toList();
        List<String> blockedUsers = blockedUserQueryRepository.findAllByUserId(newUser).stream()
                .map(blockedUser -> blockedUser.getId().toString())
                .toList();
        UserRelationDto userRelationDto = UserRelationDto.ofBlockedUserIdsAndPopupIds(blockedUsers, blockedPopups);

        // TODO: 여기까지 수정 필요

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
