package com.poppin.poppinserver.alarm.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.poppin.poppinserver.alarm.domain.InformIsRead;
import com.poppin.poppinserver.alarm.domain.PopupAlarm;
import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmTokenRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.response.AlarmStatusResponseDto;
import com.poppin.poppinserver.alarm.repository.FCMTokenRepository;
import com.poppin.poppinserver.alarm.repository.InformAlarmRepository;
import com.poppin.poppinserver.alarm.repository.InformIsReadRepository;
import com.poppin.poppinserver.alarm.repository.PopupAlarmRepository;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


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

}



