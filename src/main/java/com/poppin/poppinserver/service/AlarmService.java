package com.poppin.poppinserver.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.alarm.request.AlarmTokenRequestDto;
import com.poppin.poppinserver.dto.alarm.request.InformAlarmRequestDto;
import com.poppin.poppinserver.dto.alarm.response.InformAlarmListResponseDto;
import com.poppin.poppinserver.dto.alarm.response.InformAlarmResponseDto;
import com.poppin.poppinserver.dto.alarm.response.PopupAlarmResponseDto;
import com.poppin.poppinserver.dto.alarm.response.UnreadAlarmResponseDto;
import com.poppin.poppinserver.dto.notification.request.FCMRequestDto;
import com.poppin.poppinserver.dto.popup.response.PopupDetailDto;

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
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmService {

    private final AmazonS3Client s3Client;

    private final PopupAlarmRepository popupAlarmRepository;
    private final PopupRepository popupRepository;
    private final InformAlarmRepository informAlarmRepository;
    private final InformAlarmImageRepository informAlarmImageRepository;
    private final UserRepository userRepository;
    private final VisitRepository visitRepository;
    private final InterestRepository interestRepository;
    private final ReviewRepository reviewRepository;
    private final PosterImageRepository posterImageRepository;
    private final ReviewImageRepository reviewImageRepository;

    private final VisitorDataService visitorDataService;
    private final VisitService visitService;
    private final PopupService popupService;

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


    public UnreadAlarmResponseDto readAlarm(AlarmTokenRequestDto fcmRequestDto){
        UnreadAlarmResponseDto responseDto;
        List<InformAlarm> informAlarms = informAlarmRepository.findUnreadInformAlarms();
        List<PopupAlarm> popupAlarms = popupAlarmRepository.findUnreadPopupAlarms(fcmRequestDto.fcmToken());
        if (!informAlarms.isEmpty() && !popupAlarms.isEmpty()){
            responseDto = UnreadAlarmResponseDto.fromEntity(true);
        }
        else {
            responseDto = UnreadAlarmResponseDto.fromEntity(false);
        }
        return responseDto;
    }

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
                            .isRead(false) // 읽음 여부
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

        log.info("INFORM ALARM inserting");

        try {
            String keyword = "INFORM";
            String iconUrl = s3Client.getUrl(alarmBucket, info).toString();

            InformAlarm alarm = InformAlarm.builder()
                    .title(requestDto.title())
                    .body(requestDto.body())
                    .keyword(keyword)
                    .icon(iconUrl)
                    .createdAt(LocalDate.now())
                    .isRead(false)
                    .build();

            informAlarmRepository.save(alarm);
            InformAlarm informAlarm = informAlarmRepository.findInformAlarmOrderByIdDesc();
            return informAlarm;

        } catch (CommonException e) {
            log.error("ERROR during saving alarm : " + e.getMessage());
            return null;
        }
    }



    // 알림 - 팝업 공지사항(1 depth)
    public List<PopupAlarmResponseDto> readPopupAlarmList( Long userId, AlarmTokenRequestDto fcmRequestDto){
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

    // 알림 - 팝업 공지사항(2 depth)
    public PopupDetailDto readPopupDetail(Long userId, Long popupId){

        // 팝업 알림 isRead true 반환
        PopupAlarm popupAlarm = popupAlarmRepository.findByPopupId(popupId);
        popupAlarm.markAsRead();
        popupAlarmRepository.save(popupAlarm);

        PopupDetailDto popupDetailDto = popupService.readDetail(popupId, userId);

        return  popupDetailDto;
    }




    // 공지사항 알림 (1 depth)
    public List<InformAlarmListResponseDto> readInformAlarmList(){
        List<InformAlarmListResponseDto> informAlarmListResponseDtoList = new ArrayList<>();
        List<InformAlarm> alarmList = informAlarmRepository.findByKeywordOrderByCreatedAtDesc();

        for (InformAlarm alarm : alarmList){
            InformAlarmListResponseDto informAlarmListResponseDto = InformAlarmListResponseDto.fromEntity(alarm);
            informAlarmListResponseDtoList.add(informAlarmListResponseDto);
        }
        return informAlarmListResponseDtoList;
    }


    // 공지사항 알림 (2 depth)
    public InformAlarmResponseDto readDetailInformAlarm(Long informId){
        // informAlarm 먼저 찾고, isRead true로 저장
        InformAlarm informAlarm = informAlarmRepository.findById(informId)
                        .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_INFO_ALARM));
        informAlarm.markAsRead();
        informAlarmRepository.save(informAlarm);

        // InfoAlarmImages에서 이미지 url 찾기
        Optional<InformAlarmImage> img = informAlarmImageRepository.findByAlarmId(informId);
        if (img.isEmpty()) throw new CommonException(ErrorCode.NOT_FOUND_INFO_IMG);


        // InformAlarmResponseDto 객체 만들기
        else{
            InformAlarmResponseDto informAlarmResponseDto = InformAlarmResponseDto.builder()
                    .id(informAlarm.getId())
                    .title(informAlarm.getTitle())
                    .body(informAlarm.getBody())
                    .posterUrl(img.get().getPosterUrl())
                    .createdAt(informAlarm.getCreatedAt())
                    .build();
            return  informAlarmResponseDto;
        }
    }




    private URL getUrlForTopic(EPopupTopic topic) {
        URL url = null;
        switch (topic) {
            case MAGAM -> url = s3Client.getUrl(alarmBucket, magam);

            case CHANGE_INFO -> url =  s3Client.getUrl(alarmBucket, info);

            case JAEBO -> url =  s3Client.getUrl(alarmBucket, jaebo);

            case REOPEN -> url =  s3Client.getUrl(alarmBucket, reopen);

            case HOT -> url =  s3Client.getUrl(alarmBucket, hot);

            case KEYWORD -> url =  s3Client.getUrl(alarmBucket, key);

            case OPEN -> url =  s3Client.getUrl(alarmBucket, open);

            case HOOGI -> url =  s3Client.getUrl(alarmBucket, hoogi);

            case BANGMUN -> url =  s3Client.getUrl(alarmBucket, bangmun);

            default -> url =  null;

        }
        log.info("Generated URL for topic {}: {}", topic, url);
        return url;
    }


}



