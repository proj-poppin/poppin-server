package com.poppin.poppinserver.alarm.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.domain.InformAlarm;
import com.poppin.poppinserver.alarm.domain.InformIsRead;
import com.poppin.poppinserver.alarm.domain.PopupAlarm;
import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmTokenRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.request.InformAlarmCreateRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.response.AlarmStatusResponseDto;
import com.poppin.poppinserver.alarm.dto.popupAlarm.request.PopupAlarmDto;
import com.poppin.poppinserver.alarm.repository.FCMTokenRepository;
import com.poppin.poppinserver.alarm.repository.InformAlarmRepository;
import com.poppin.poppinserver.alarm.repository.InformIsReadRepository;
import com.poppin.poppinserver.alarm.repository.PopupAlarmRepository;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmService {

    private final AmazonS3Client s3Client;

    private final PopupAlarmRepository popupAlarmRepository;
    private final PopupRepository popupRepository;
    private final InformAlarmRepository informAlarmRepository;
    private final InformIsReadRepository informIsReadRepository;
    private final FCMTokenRepository fcmTokenRepository;

    @Value("${cloud.aws.s3.alarm.bucket.name}")
    private String alarmBucket;

    /**
     * 홈 화면 진입 시 읽지 않은 공지 여부 판단
     *
     * @param fcmRequestDto :  FCM Token request dto
     * @return : UnreadAlarmResponseDto
     */
    //TODO : 삭제 예정
    public AlarmStatusResponseDto readAlarm(AlarmTokenRequestDto fcmRequestDto) {
        AlarmStatusResponseDto responseDto;

        List<InformIsRead> informAlarms = informIsReadRepository.findUnreadInformAlarms(fcmRequestDto.fcmToken()); // 공지
        List<PopupAlarm> popupAlarms = popupAlarmRepository.findUnreadPopupAlarms(fcmRequestDto.fcmToken()); // 팝업

        if (!informAlarms.isEmpty() || !popupAlarms.isEmpty()) {
            responseDto = AlarmStatusResponseDto.fromEntity(true);
        } else {
            responseDto = AlarmStatusResponseDto.fromEntity(false);
        }
        return responseDto;
    }

    // 알림 - 팝업 공지사항 등록
    public String insertPopupAlarm(PopupAlarmDto popupAlarmDto) {

        log.info("POPUP ALARM insert");

        try {
            Popup popup = popupRepository.findById(popupAlarmDto.popupId().getId())
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

            String url = Objects.requireNonNull(getUrlForTopic(popupAlarmDto.topic())).toString();

            PopupAlarm alarm = PopupAlarm.builder()
                    .popupId(popup)
                    .token(popupAlarmDto.fcmToken())
                    .title(popupAlarmDto.title())
                    .body(popupAlarmDto.body())
                    .icon(url)
                    .createdAt(LocalDate.now())
                    .isRead(false) // 읽음 여부
                    .build();

            popupAlarmRepository.save(alarm);

            return "1";
        } catch (CommonException e) {
            log.error("ERROR during saving alarm : " + e.getMessage());
            return "0";
        }
    }

    // 알림 - 일반 공지사항 등록
    public InformAlarm insertInformAlarm(InformAlarmCreateRequestDto requestDto) {

        log.info("INFORM ALARM insert");

        try {
            String iconUrl = s3Client.getUrl(alarmBucket, EPopupTopic.CHANGE_INFO.getImgName()).toString();

            InformAlarm alarm = InformAlarm.builder()
                    .title(requestDto.title())
                    .body(requestDto.body())
                    .icon(iconUrl)
                    .createdAt(LocalDate.now())
                    .build();

            informAlarmRepository.save(alarm);
            InformAlarm informAlarm = informAlarmRepository.findInformAlarmOrderByIdDesc();
            return informAlarm;

        } catch (CommonException e) {
            log.error("ERROR during saving alarm : " + e.getMessage());
            return null;
        }
    }

    public List<InformAlarm> getInformAlarms(Long userId) {
        FCMToken fcmToken = fcmTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_FCM_TOKEN));

        List<InformAlarm> informAlarms = informAlarmRepository.findByKeywordOrderByIdDesc(fcmToken.getToken());
        return informAlarms;
    }

    private URL getUrlForTopic(EPopupTopic topic) {
        URL url = s3Client.getUrl(alarmBucket, topic.getImgName());
        log.info("Generated URL for topic {}: {}", topic, url);
        return url;
    }
}



