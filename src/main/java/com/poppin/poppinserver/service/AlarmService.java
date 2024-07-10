package com.poppin.poppinserver.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.alarm.request.*;
import com.poppin.poppinserver.dto.alarm.response.*;
import com.poppin.poppinserver.dto.fcm.request.FCMRequestDto;
import com.poppin.poppinserver.dto.popup.response.PopupDetailDto;

import com.poppin.poppinserver.dto.popup.response.PopupGuestDetailDto;
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

import static com.poppin.poppinserver.util.FCMTokenUtil.refreshToken;

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
    private final InformIsReadRepository informIsReadRepository;
    private final FCMTokenRepository fcmTokenRepository;

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


    /**
     *  홈 화면 진입 시 읽지 않은 공지 여부 판단
     * @param fcmRequestDto :  FCM Token request dto
     * @return : UnreadAlarmResponseDto
     */
    public AlarmStatusResponseDto readAlarm(AlarmTokenRequestDto fcmRequestDto){
        AlarmStatusResponseDto responseDto;

        List<InformIsRead> informAlarms = informIsReadRepository.findUnreadInformAlarms(fcmRequestDto.fcmToken()); // 공지
        List<PopupAlarm> popupAlarms = popupAlarmRepository.findUnreadPopupAlarms(fcmRequestDto.fcmToken()); // 팝업

        if (!informAlarms.isEmpty() || !popupAlarms.isEmpty()){
            responseDto = AlarmStatusResponseDto.fromEntity(true);
        }
        else {
            responseDto = AlarmStatusResponseDto.fromEntity(false);
        }
        return responseDto;
    }

    public String insertPopupAlarm(FCMRequestDto fcmRequestDto) {

        log.info("POPUP ALARM insert");

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
    public InformAlarm insertInformAlarm(InformAlarmCreateRequestDto requestDto) {

        log.info("INFORM ALARM insert");

        try {
            String keyword = "INFORM";
            String iconUrl = s3Client.getUrl(alarmBucket, info).toString();

            InformAlarm alarm = InformAlarm.builder()
                    .title(requestDto.title())
                    .body(requestDto.body())
                    .keyword(keyword)
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


    // 알림 - 팝업 공지사항(1 depth)
    public List<PopupAlarmResponseDto> readPopupAlarmList( AlarmTokenRequestDto fcmRequestDto){


        log.info("fcm token : {} " , fcmRequestDto.fcmToken());
        List<PopupAlarmResponseDto> popupAlarmResponseDtoList = new ArrayList<>();
        List<PopupAlarm> alarmList = popupAlarmRepository.findByKeywordOrderByCreatedAtDesc(fcmRequestDto.fcmToken());

        for (PopupAlarm alarm : alarmList){
            log.info("alarmList : {} " , alarmList);
            PopupAlarmResponseDto popupAlarmResponseDto = PopupAlarmResponseDto.fromEntity(alarm);
            popupAlarmResponseDtoList.add(popupAlarmResponseDto);
        }
        log.info("result : {} " , popupAlarmResponseDtoList);
        return popupAlarmResponseDtoList;
    }

    // 알림 - 로그인 팝업 공지사항(2 depth)
    public PopupDetailDto readPopupDetail(Long userId, AlarmPopupRequestDto requestDto){

        log.info("alarm popup login detail ...");

        Long alarmId = requestDto.alarmId();
        Long popupId = requestDto.popupId();
        String fcmToken = requestDto.fcmToken();

        log.info("alarm id : {}" + alarmId);
        log.info("popup id : {}" + popupId);
        log.info("fcm token : {}" + fcmToken);

        // 팝업 알림 isRead true 반환
        PopupAlarm popupAlarm = popupAlarmRepository.findById(alarmId)
                .orElseThrow(()-> new CommonException(ErrorCode.NOT_FOUND_POPUP_ALARM));
        popupAlarm.markAsRead();
        popupAlarmRepository.save(popupAlarm);

        // fcm token refresh
        FCMToken token = fcmTokenRepository.findByToken(fcmToken);
        refreshToken(token);

        // 팝업 상세 load
        PopupDetailDto popupDetailDto = popupService.readDetail(requestDto.popupId(), userId);

        return  popupDetailDto;

    }

    // 알림 - 비 로그인 팝업 공지사항(2 depth)
    public PopupGuestDetailDto readPopupDetailGuest(AlarmPopupRequestDto requestDto){

        log.info("alarm popup un-login detail ...");

        Long alarmId = requestDto.alarmId();
        Long popupId = requestDto.popupId();
        String fcmToken = requestDto.fcmToken();

        log.info("alarm id : {}" + alarmId);
        log.info("popup id : {}" + popupId);
        log.info("fcm token : {}" + fcmToken);


        // 팝업 알림 isRead true 반환
        PopupAlarm popupAlarm = popupAlarmRepository.findById(alarmId)
                .orElseThrow(()-> new CommonException(ErrorCode.NOT_FOUND_POPUP_ALARM));
        popupAlarm.markAsRead();
        popupAlarmRepository.save(popupAlarm);

        // fcm token refresh
        FCMToken token = fcmTokenRepository.findByToken(fcmToken);
        refreshToken(token);

        // 팝업 상세 정보
        PopupGuestDetailDto popupDetailDto = popupService.readGuestDetail(requestDto.popupId());

        return  popupDetailDto;
    }


    // 공지사항 알림 (1 depth)
    public List<InformAlarmListResponseDto> readInformAlarmList(AlarmTokenRequestDto requestDto){

        log.info("read inform alarm ...");

        log.info("fcm token : {} " + requestDto.fcmToken());

        List<InformAlarmListResponseDto> informAlarmListResponseDtoList = new ArrayList<>();
        List<InformAlarm> alarmList = informAlarmRepository.findByKeywordOrderByCreatedAtDesc(requestDto.fcmToken());

        log.info("alarm list : {}" + alarmList);

        for (InformAlarm alarm : alarmList){
            InformIsRead informIsRead = informIsReadRepository.findByFcmTokenAndInformAlarm(requestDto.fcmToken(), alarm.getId());
            InformAlarmListResponseDto informAlarmListResponseDto = InformAlarmListResponseDto.fromEntity(alarm, informIsRead.getIsRead());
            informAlarmListResponseDtoList.add(informAlarmListResponseDto);
        }
        return informAlarmListResponseDtoList;
    }


    // 공지사항 알림 (2 depth)
    public InformAlarmResponseDto readInformDetail(InformDetailDto requestDto){

        String fcmToken = requestDto.fcmToken();
        Long informId = requestDto.informId();

        log.info("dto : {}" , requestDto);
        log.info("fcmToken : {}", fcmToken );
        log.info("inform ID : {}", informId);

        // isRead
        InformIsRead informIsRead = informIsReadRepository.findByFcmTokenAndInformAlarm(fcmToken,informId);
        log.info("inform : {}", informIsRead);

        informIsRead.markAsRead();
        informIsReadRepository.save(informIsRead);

        // informAlarm
        InformAlarm informAlarm = informAlarmRepository.findById(informId)
                        .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_INFO_ALARM));

        // informAlarm img
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


    // 공지사항 읽음 여부 테이블에 유저 정보와 함께 저장
    public void insertInformIsRead(FCMToken token, InformAlarm informAlarm){
        InformIsRead informIsRead = new InformIsRead(informAlarm, token);
        informIsReadRepository.save(informIsRead);
    }

    public UnreadAlarmsResponseDto countUnreadAlarms(String fcmToken){

        int resultCount;

        int unreadInformAlarms = informIsReadRepository.unreadInforms(fcmToken);
        int unreadPopupAlarms = popupAlarmRepository.UnreadPopupAlarms(fcmToken);

        resultCount = unreadInformAlarms + unreadPopupAlarms;

        UnreadAlarmsResponseDto responseDto = UnreadAlarmsResponseDto.fromEntity(resultCount);

        return responseDto;
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



