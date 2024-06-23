package com.poppin.poppinserver.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.alarm.request.FcmTokenAlarmRequestDto;
import com.poppin.poppinserver.dto.alarm.request.InformAlarmRequestDto;
import com.poppin.poppinserver.dto.alarm.response.InformAlarmResponseDto;
import com.poppin.poppinserver.dto.alarm.response.PopupAlarmResponseDto;
import com.poppin.poppinserver.dto.notification.request.FCMRequestDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.*;
import com.poppin.poppinserver.type.EPopupTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmService {

    private final AmazonS3Client s3Client;

    private final PopupAlarmRepository popupAlarmRepository;
    private final PopupRepository popupRepository;
    private final InformAlarmRepository informAlarmRepository;

    private final UserRepository userRepository;

    @Value("${cloud.aws.s3.alarm.bucket.name}")
    private String alarmBucket;

    @Value("${cloud.aws.s3.alarm.icon.magam}")
    private String magam;

    @Value("${cloud.aws.s3.alarm.icon.info}")
    private String info;

    @Value("${cloud.aws.s3.alarm.icon.jaebo}")
    private String jaebo;

    @Value("${cloud.aws.s3.alarm.icon.reopen}")
    private String reopen;

    @Value("${cloud.aws.s3.alarm.icon.hot}")
    private String hot;

    @Value("${cloud.aws.s3.alarm.icon.keyword}")
    private String key;

    @Value("${cloud.aws.s3.alarm.icon.open}")
    private String open;

    @Value("${cloud.aws.s3.alarm.icon.hoogi}")
    private String hoogi;

    @Value("${cloud.aws.s3.alarm.icon.bangmun}")
    private String bangmun;

    public String insertPopupAlarmKeyword(FCMRequestDto fcmRequestDto) {

        log.info("POPUP ALARM inserting \n");

        try {

            for (EPopupTopic topicType : EPopupTopic.values()) {

                if (topicType.equals(fcmRequestDto.topic())) {
                    Popup popup = popupRepository.findById(fcmRequestDto.popupId())
                            .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

                    String keyword = "POPUP"; // 팝업 알림

                    String url = Objects.requireNonNull(getUrlForTopic(fcmRequestDto.topic())).toString();

                    PopupAlarm alarm = PopupAlarm.builder()
                            .popupId(popup)
                            .token(fcmRequestDto.token())
                            .title(fcmRequestDto.title())
                            .body(fcmRequestDto.body())
                            .keyword(keyword)
                            .icon(url)
                            .createdAt(LocalDate.now())
                            .build();

                    popupAlarmRepository.save(alarm);
                }
            }
            return "1";
        } catch (CommonException e) {
            log.error("ERROR during saving alarm : " + e.getMessage());
            return "0";
        }
    }

    // 알림 - 일반 공지사항 등록
    public InformAlarm insertInformAlarmKeyword(InformAlarmRequestDto requestDto) {

        log.info("INFORM ALARM inserting \n");

        try {
            String keyword = "INFORM";
            String iconUrl = s3Client.getUrl(alarmBucket, info).toString();

            InformAlarm alarm = new InformAlarm(
                    requestDto.title(),
                    requestDto.body(),
                    keyword,
                    iconUrl,
                    LocalDate.now()
            );
            informAlarmRepository.save(alarm);
            InformAlarm informAlarm = informAlarmRepository.findInformAlarmOrderByIdDesc();
            return informAlarm;

        } catch (CommonException e) {
            log.error("ERROR during saving alarm : " + e.getMessage());
            return null;
        }
    }



    // 알림 - 팝업 공지사항 보기
    public List<PopupAlarmResponseDto> readPopupAlarmList( Long userId, FcmTokenAlarmRequestDto fcmRequestDto){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        log.info("fcm token : {} " + fcmRequestDto.fcmToken());
        List<PopupAlarmResponseDto> popupAlarmResponseDtoList = new ArrayList<>();
        List<PopupAlarm> alarmList = popupAlarmRepository.findByKeywordOrderByCreatedAtDesc(fcmRequestDto.fcmToken());

        for (PopupAlarm alarm : alarmList){
            log.info("alarmList : " + alarmList);
            PopupAlarmResponseDto popupAlarmResponseDto = PopupAlarmResponseDto.fromEntity(alarm);
            popupAlarmResponseDtoList.add(popupAlarmResponseDto);
        }
        log.info("result : " + popupAlarmResponseDtoList);
        return popupAlarmResponseDtoList;
    }

    public List<InformAlarmResponseDto> readInformAlarmList(){
        List<InformAlarmResponseDto> informAlarmResponseDtoList = new ArrayList<>();
        List<InformAlarm> alarmList = informAlarmRepository.findByKeywordOrderByCreatedAtDesc();

        for (InformAlarm alarm : alarmList){
            InformAlarmResponseDto informAlarmResponseDto = InformAlarmResponseDto.fromEntity(alarm);
            informAlarmResponseDtoList.add(informAlarmResponseDto);
        }
        return informAlarmResponseDtoList;
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
            case BANGMUN -> {
                return s3Client.getUrl(alarmBucket, bangmun);
            }
            default -> {
                return null;
            }
        }
    }


}



