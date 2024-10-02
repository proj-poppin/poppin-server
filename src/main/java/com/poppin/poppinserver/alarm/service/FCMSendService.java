package com.poppin.poppinserver.alarm.service;

import com.google.firebase.messaging.*;
import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.domain.InformAlarm;
import com.poppin.poppinserver.alarm.domain.UserAlarmKeyword;
import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmKeywordCreateRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.request.InformAlarmCreateRequestDto;
import com.poppin.poppinserver.alarm.dto.fcm.request.FCMRequestDto;
import com.poppin.poppinserver.alarm.dto.popupAlarm.request.PopupAlarmDto;
import com.poppin.poppinserver.alarm.repository.FCMTokenRepository;
import com.poppin.poppinserver.core.config.APNsConfiguration;
import com.poppin.poppinserver.core.config.AndroidConfiguration;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.core.type.EPushInfo;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.review.domain.Review;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.poppin.poppinserver.core.util.FCMRefreshUtil.refreshToken;

@Slf4j
@RequiredArgsConstructor
@Service
public class FCMSendService {

    private final FirebaseMessaging firebaseMessaging;
    private final APNsConfiguration apnsConfiguration;
    private final AndroidConfiguration androidConfiguration;
    private final PopupRepository popupRepository;
    private final FCMTokenRepository fcmTokenRepository;
    private final AlarmService alarmService;

    //공지사항
    public void sendInformationByFCMToken(List<FCMToken> tokenList, InformAlarmCreateRequestDto requestDto,
                                          InformAlarm informAlarm) {

        for (FCMToken token : tokenList) {
            log.info("token : " + token.getToken());
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(requestDto.title())
                            .setBody(requestDto.body())
                            .build())
                    .setApnsConfig(apnsConfiguration.apnsConfig())
                    .setAndroidConfig(androidConfiguration.androidConfig())
                    .setToken(token.getToken())
                    .putData("id", informAlarm.getId().toString())
                    .putData("type", "inform")
                    .build();
            try {
                String result = firebaseMessaging.send(message);
                log.info("Successfully sent message: " + result);
                refreshToken(token); // 토큰일자 갱신
            } catch (FirebaseMessagingException e) {
                log.error("Failed to send message: " + e.getMessage());
            }
        }
    }


    //인기팝업, 재오픈 팝업 알림 전송 메서드
    public void sendAlarmByFCMToken(List<Popup> popupList, EPushInfo info) {

        List<Long> popupIdList = new ArrayList<>();
        for (Popup p : popupList) {
            popupIdList.add(p.getId());
        }

        List<FCMToken> tokenList = fcmTokenRepository.findAll();

        for (FCMToken token : tokenList) {
            log.info("token : " + token.getToken());
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(info.getTitle())
                            .setBody(info.getBody())
                            .build())
                    .setApnsConfig(apnsConfiguration.apnsConfig())
                    .setAndroidConfig(androidConfiguration.androidConfig())
                    .setToken(token.getToken())
                    .putData("popupList", popupIdList.toString())
                    .build();

            try {
                String result = firebaseMessaging.send(message);
                log.info(" Successfully sent message: " + result);

                refreshToken(token);

                // 팝업 알림 등록(리스트)
                for (Popup p : popupList) {
                    // 파라미터 EPushInfo에 따라 재오픈, 인기 토픽 지정
                    EPopupTopic topic = (info.equals(EPushInfo.REOPEN)) ? EPopupTopic.REOPEN : EPopupTopic.HOT;

                    PopupAlarmDto popupAlarmDto = PopupAlarmDto.fromEntity(
                            p,
                            token.getToken(),
                            info.getTitle(),
                            info.getBody(),
                            topic
                    );
                    // 등록&로그 메서드
                    logAlarmStatus(popupAlarmDto);
                }

            } catch (FirebaseMessagingException e) {
                log.error("Failed to send message: " + e.getMessage());
            }
        }
    }

    //후기 추천 앱푸시 메서드
    public void sendChoochunByFCMToken(Popup popup, Review review, EPushInfo info) {

        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(info.getTitle())
                        .setBody("[" + review.getPopup().getName() + "] " + info.getBody())
                        .build())
                .setApnsConfig(apnsConfiguration.apnsConfig())
                .setAndroidConfig(androidConfiguration.androidConfig())
                .setToken(review.getToken())
                .putData("id", review.getPopup().getId().toString())
                .putData("type", "popup")
                .build();

        try {
            String result = firebaseMessaging.send(message);
            log.info("Successfully sent message: " + result);

            FCMToken token = fcmTokenRepository.findByToken(review.getToken());
            refreshToken(token);

        } catch (FirebaseMessagingException e) {
            log.error("Failed to send message: " + e.getMessage());
        }

        PopupAlarmDto popupAlarmDto = PopupAlarmDto.fromEntity(
                popup,
                review.getToken(),
                info.getTitle(),
                info.getBody(),
                EPopupTopic.CHOOCHUN
        );
        // 등록&로그 메서드
        logAlarmStatus(popupAlarmDto);
    }

    // 키워드 알림 전송
    public void sendKeywordAlarmByFCMToken(FCMToken token, AlarmKeywordCreateRequestDto requestDto,
                                           UserAlarmKeyword userAlarmKeyword) {
        log.info("token : " + token.getToken());
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(requestDto.title())
                        .setBody(requestDto.body())
                        .build())
                .setApnsConfig(apnsConfiguration.apnsConfig())
                .setAndroidConfig(androidConfiguration.androidConfig())
                .setToken(token.getToken())
                .putData("id", userAlarmKeyword.getId().toString())
                .putData("type", "keyword")
                .build();
        try {
            String result = firebaseMessaging.send(message);
            log.info("Successfully sent message: " + result);
            refreshToken(token); // 토큰일자 갱신
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send message: " + e.getMessage());
        }
    }

    // FCM 토픽 메시지 전송 메서드
    public void sendFCMTopicMessage(List<FCMRequestDto> fcmRequestDtoList) {

        for (FCMRequestDto fcmRequestDto : fcmRequestDtoList) {

            Message message;

            Popup popup = popupRepository.findById(fcmRequestDto.popupId())
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

            message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(fcmRequestDto.title())
                            .setBody("[" + popup.getName() + "] " + fcmRequestDto.body())
                            .build())
                    .setTopic(String.valueOf(fcmRequestDto.topic()))
                    .setAndroidConfig(androidConfiguration.androidConfig())
                    .setApnsConfig(apnsConfiguration.apnsConfig())
                    .putData("id", fcmRequestDto.popupId().toString())
                    .putData("type", "popup")
                    .build();

            log.info("TOPIC message sending...");
            try {

                String result = firebaseMessaging.send(message);
                log.debug("Successfully sent message: " + result);

                FCMToken token = fcmTokenRepository.findByToken(fcmRequestDto.token());
                refreshToken(token); // 토큰 갱신

                // 팝업 알림 등록
                PopupAlarmDto popupAlarmDto = PopupAlarmDto.fromEntity(
                        popup,
                        fcmRequestDto.token(),
                        fcmRequestDto.title(),
                        fcmRequestDto.body(),
                        fcmRequestDto.topic()
                );
                logAlarmStatus(popupAlarmDto);
            } catch (FirebaseMessagingException e) {
                log.error("Failed to send message: " + e.getMessage());
            }
        }

    }

    // 여러기기 FCM 동시 전송 메서드
    public void sendMultiDeviceMessage(List<FCMRequestDto> fcmRequestDtoList) throws FirebaseMessagingException {
        List<String> tokenList = null;
        for (FCMRequestDto fcmRequestDto : fcmRequestDtoList) {
            tokenList = IntStream.rangeClosed(1, 1000)
                    .mapToObj(index -> fcmRequestDto.token())
                    .collect(Collectors.toList());
        }
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(fcmRequestDtoList.get(0).title())
                        .setBody(fcmRequestDtoList.get(0).body())
                        .build()
                )
                .addAllTokens(tokenList)
                .build();
        if (message != null) {
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);

            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                List<String> failedTokens = new ArrayList<>();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        failedTokens.add(tokenList.get(i));
                    }
                }
                log.error(" List of tokens that caused failures: " + failedTokens);
            } else {
                log.info(" List of tokens send messages SUCCESSFULLY");
            }
        }
    }

    // 팝업 알림 리스트 생성 & 로그 남기는 메서드
    private void logAlarmStatus(PopupAlarmDto popupAlarmDto) {
        String flag = alarmService.insertPopupAlarm(popupAlarmDto);
        if (flag.equals("1")) {
            log.info(popupAlarmDto.fcmToken() + " alarm success");
        } else {
            log.error(popupAlarmDto.fcmToken() + " alarm fail");
        }
    }
}
