package com.poppin.poppinserver.alarm.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.domain.InformAlarm;
import com.poppin.poppinserver.alarm.domain.InformIsRead;
import com.poppin.poppinserver.alarm.domain.PopupAlarm;
import com.poppin.poppinserver.alarm.dto.alarm.request.InformAlarmCreateRequestDto;
import com.poppin.poppinserver.alarm.dto.popupAlarm.request.PopupAlarmDto;
import com.poppin.poppinserver.alarm.repository.InformAlarmRepository;
import com.poppin.poppinserver.alarm.repository.InformIsReadRepository;
import com.poppin.poppinserver.alarm.repository.PopupAlarmRepository;
import com.poppin.poppinserver.alarm.usecase.AlarmCommandUseCase;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmCommandService implements AlarmCommandUseCase {
    private final AmazonS3Client s3Client;

    //TODO: @정구연 레포->유스케이스 부탁합니다.
    private final PopupRepository popupRepository;
    private final PopupAlarmRepository popupAlarmRepository;
    private final InformAlarmRepository informAlarmRepository;
    private final InformIsReadRepository informIsReadRepository;

    @Value("${cloud.aws.s3.alarm.bucket.name}")
    private String alarmBucket;

    @Override
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

    @Override
    public String insertPopupAlarm(PopupAlarmDto popupAlarmDto) {
        log.info("POPUP ALARM insert");

        try {
            //TODO: @정구연 레포->유스케이스 부탁합니다.
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

    @Override
    public void insertInformIsRead(FCMToken token, InformAlarm informAlarm) {
        InformIsRead informIsRead = new InformIsRead(informAlarm, token);
        informIsReadRepository.save(informIsRead);
    }

    @Override
    public URL getUrlForTopic(EPopupTopic topic) {
        URL url = s3Client.getUrl(alarmBucket, topic.getImgName());
        log.info("Generated URL for topic {}: {}", topic, url);
        return url;
    }
}
