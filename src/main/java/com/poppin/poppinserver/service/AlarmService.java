package com.poppin.poppinserver.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.poppin.poppinserver.domain.Alarm;
import com.poppin.poppinserver.domain.AlarmKeyword;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.dto.notification.request.FCMRequestDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.AlarmKeywordRepository;
import com.poppin.poppinserver.repository.AlarmRepository;
import com.poppin.poppinserver.repository.PopupRepository;
import com.poppin.poppinserver.type.EPopupTopic;
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
public class AlarmService {

    private final AmazonS3Client s3Client;

    private final AlarmRepository alarmRepository;
    private final PopupRepository popupRepository;

    @Value("${cloud.s3.alarm.bucket.name}")
    private String alarmBucket;

    @Value("${cloud.s3.alarm.icon.magam}")
    private String magam;

    @Value("${cloud.s3.alarm.icon.info}")
    private String info;

    @Value("${cloud.s3.alarm.icon.jaebo}")
    private String jaebo;

    @Value("${cloud.s3.alarm.icon.reopen}")
    private String reopen;

    @Value("${cloud.s3.alarm.icon.hot}")
    private String hot;

    @Value("${cloud.s3.alarm.icon.keyword}")
    private String key;

    @Value("${cloud.s3.alarm.icon.open}")
    private String open;

    @Value("${cloud.s3.alarm.icon.hoogi}")
    private String hoogi;

    public String insertAlarmKeyword(FCMRequestDto fcmRequestDto) {

        log.info("--- 알람 저장 시작  ---");

        try {

            for (EPopupTopic topicType : EPopupTopic.values()) {

                if (topicType.equals(fcmRequestDto.topic())) {
                    Popup popup = popupRepository.findById(fcmRequestDto.popupId())
                            .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

                    String keyword = "POPUP";

                    String url = Objects.requireNonNull(getUrlForTopic(fcmRequestDto.topic())).toString();

                    Alarm alarm = Alarm.builder()
                            .popupId(popup)
                            .title(fcmRequestDto.title())
                            .body(fcmRequestDto.body())
                            .keyword(keyword)
                            .url(url)
                            .createdAt(LocalDate.now())
                            .build();

                    alarmRepository.save(alarm);
                }
            }
            return "1";
        } catch (CommonException e) {
            log.error("--- 알림 키워드 저장 중 오류 발생 ---\n" + e.getMessage());
            return "0";
        }
    }


    private URL getUrlForTopic(EPopupTopic topic) {
        switch (topic) {
            case MAGAM -> {
                return s3Client.getUrl(alarmBucket, magam);
            }
            case CHANGE_INFO -> {
                return s3Client.getUrl(alarmBucket, info);
            }
            case JAEBO -> {
                return s3Client.getUrl(alarmBucket, jaebo);
            }
            case REOPEN -> {
                return s3Client.getUrl(alarmBucket, reopen);
            }
            case HOT -> {
                return s3Client.getUrl(alarmBucket, hot);
            }
            case KEYWORD -> {
                return s3Client.getUrl(alarmBucket, key);
            }
            case OPEN -> {
                return s3Client.getUrl(alarmBucket, open);
            }
            case HOOGI -> {
                return s3Client.getUrl(alarmBucket, hoogi);
            }
            default -> {
                return null;
            }
        }
    }
}



