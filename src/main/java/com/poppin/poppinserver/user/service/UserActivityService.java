package com.poppin.poppinserver.user.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.poppin.poppinserver.alarm.domain.InformAlarm;
import com.poppin.poppinserver.alarm.domain.PopupAlarm;
import com.poppin.poppinserver.alarm.domain.UserInformAlarm;
import com.poppin.poppinserver.alarm.domain.type.ENotificationCategory;
import com.poppin.poppinserver.alarm.dto.DestinationResponseDto;
import com.poppin.poppinserver.alarm.dto.NotificationResponseDto;
import com.poppin.poppinserver.alarm.repository.PopupAlarmRepository;
import com.poppin.poppinserver.alarm.repository.UserInformAlarmRepository;
import com.poppin.poppinserver.interest.domain.Interest;
import com.poppin.poppinserver.interest.repository.InterestRepository;
import com.poppin.poppinserver.popup.domain.Waiting;
import com.poppin.poppinserver.popup.dto.popup.response.PopupActivityResponseDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupScrapDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupWaitingDto;
import com.poppin.poppinserver.popup.repository.WaitingRepository;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.dto.user.response.UserNoticeResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserNotificationResponseDto;
import com.poppin.poppinserver.visit.domain.Visit;
import com.poppin.poppinserver.visit.dto.visit.response.VisitDto;
import com.poppin.poppinserver.visit.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 유저의 알람 내역, 방문 팝업, 관심 팝업, 오픈 대기 팝업 정보를 조회
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserActivityService {

    private final PopupAlarmRepository popupAlarmRepository;
    private final UserInformAlarmRepository userInformAlarmRepository;
    private final InterestRepository interestRepository;
    private final VisitRepository visitRepository;
    private final WaitingRepository waitingRepository;

    private final AmazonS3Client s3Client;
    @Value("${cloud.aws.s3.alarm.bucket.name}")
    private String alarmBucket;

    public PopupActivityResponseDto getPopupActivity(User user) {
        // 유저가 등록한 관심 팝업 조회
        List<Interest> userInterestPopupList = interestRepository.findByUserId(user.getId());
        List<PopupScrapDto> popupScrapDtoList = userInterestPopupList.stream().map(
                PopupScrapDto::fromInterest
        ).toList();

        // 유저가 방문한 팝업 조회
        List<Visit> userVisitedPopupList = visitRepository.findAllByUserId(user.getId());
        List<VisitDto> userVisitedPopupDtoList = userVisitedPopupList.stream()
                .map(
                        v -> VisitDto.fromEntity(
                                v.getId(), v.getPopup().getId(), v.getUser().getId(), v.getCreatedAt().toString()
                        )
                ).toList();

        // 유저가 등록한 오픈 대기 중인 팝업 조회
        List<Waiting> userWaitingPopupList = waitingRepository.findAllByUserId(user.getId());
        List<PopupWaitingDto> userWaitingPopupDtoList = userWaitingPopupList.stream()
                .map(
                        pw -> PopupWaitingDto.fromEntity(
                                pw.getId(), pw.getPopup().getId()
                        )
                ).toList();

        return PopupActivityResponseDto.fromProperties(
                popupScrapDtoList,
                userVisitedPopupDtoList,
                userWaitingPopupDtoList
        );
    }

    public UserNotificationResponseDto getUserNotificationActivity(User user, String fcmToken) {
        DestinationResponseDto destinationResponseDto = DestinationResponseDto.fromProperties(
                null, null, null, null,
                null, null, null, null, null
        );

        Long userId = user.getId();

        List<PopupAlarm> userPopupAlarm = popupAlarmRepository.findAllByUser(userId);
        List<UserInformAlarm> userInformAlarm = userInformAlarmRepository.findAllByUser(userId);

        // 유저의 팝업 관련 알람 조회
        List<NotificationResponseDto> popupNotificationResponseDtoList = userPopupAlarm.stream().map(
                popupAlarm -> NotificationResponseDto.fromProperties(
                        String.valueOf(popupAlarm.getId()),
                        String.valueOf(user.getId()),
                        null,
                        String.valueOf(ENotificationCategory.POPUP),
                        popupAlarm.getTitle(),
                        popupAlarm.getBody(),
                        null,
                        popupAlarm.getIcon(),
                        popupAlarm.getIsRead(),
                        String.valueOf(popupAlarm.getCreatedAt()),
                        String.valueOf(popupAlarm.getPopup().getId()),
                        null,
                        destinationResponseDto
                )
        ).toList();


        // 유저의 공지 관련 알람 조회
        List<NotificationResponseDto> noticeNotificationResponseDtoList = userInformAlarm.stream()
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
                            informAlarm.getIcon(),
                            isRead,
                            String.valueOf(informAlarm.getCreatedAt()),
                            null,
                            String.valueOf(informAlarm.getId()),
                            destinationResponseDto
                    );
                }).toList();

        return UserNotificationResponseDto.fromDtoList(
                popupNotificationResponseDtoList,
                noticeNotificationResponseDtoList
        );
    }

    public UserNoticeResponseDto getUserNotificationStatus(Long userId) {
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

        return UserNoticeResponseDto.of(informLastCheckedTime, checkedNoticeIds);
    }
}
