package com.poppin.poppinserver.alarm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.domain.InformAlarm;
import com.poppin.poppinserver.alarm.domain.UserAlarmKeyword;
import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmKeywordCreateRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.request.InformAlarmCreateRequestDto;
import com.poppin.poppinserver.alarm.dto.fcm.request.FCMRequestDto;
import com.poppin.poppinserver.alarm.dto.popupAlarm.request.PopupAlarmDto;
import com.poppin.poppinserver.alarm.usecase.AlarmCommandUseCase;
import com.poppin.poppinserver.alarm.usecase.AlarmListQueryUseCase;
import com.poppin.poppinserver.alarm.usecase.SendAlarmCommandUseCase;
import com.poppin.poppinserver.alarm.usecase.TokenQueryUseCase;
import com.poppin.poppinserver.core.config.APNsConfiguration;
import com.poppin.poppinserver.core.config.AndroidConfiguration;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.core.type.EPushInfo;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.poppin.poppinserver.core.util.FCMRefreshUtil.refreshToken;

@Slf4j
@RequiredArgsConstructor
@Service
public class SendAlarmCommandService implements SendAlarmCommandUseCase {
    private final FirebaseMessaging firebaseMessaging;
    private final APNsConfiguration apnsConfiguration;
    private final AndroidConfiguration androidConfiguration;

    //TODO: @정구연 popupRepository-> popupUsecase 수정 부탁드립니다.
    private final PopupRepository popupRepository;

    private final TokenQueryUseCase tokenQueryUseCase;
    private final AlarmListQueryUseCase alarmListQueryUseCase;
    private final AlarmCommandUseCase alarmCommandUseCase;

    @Override
    public void sendInformationAlarm(List<FCMToken> tokenList, InformAlarmCreateRequestDto requestDto, InformAlarm informAlarm) {
        for (FCMToken token : tokenList) {

            User user = tokenQueryUseCase.findUserByToken(token.getToken());
            Long userId = user.getId();

            int badge = alarmListQueryUseCase.countUnreadAlarms(userId);

            log.info("token : " + token.getToken());
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(requestDto.title())
                            .setBody(requestDto.body())
                            .build())
                    .setApnsConfig(apnsConfiguration.apnsConfig(badge))
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

    @Override
    public void sendScheduledPopupAlarm(List<Popup> popupList, EPushInfo info) {
        List<Long> popupIdList = new ArrayList<>();
        for (Popup p : popupList) {
            popupIdList.add(p.getId());
        }

        List<FCMToken> tokenList = tokenQueryUseCase.findAll();

        for (FCMToken token : tokenList) {

            User user = tokenQueryUseCase.findUserByToken(token.getToken());
            Long userId = user.getId();

            int badge = alarmListQueryUseCase.countUnreadAlarms(userId);
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(info.getTitle())
                            .setBody(info.getBody())
                            .build())
                    .setApnsConfig(apnsConfiguration.apnsConfig(badge))
                    .setAndroidConfig(androidConfiguration.androidConfig())
                    .setToken(token.getToken())
                    .putData("popupList", popupIdList.toString())
                    .build();

            try {
                String result = firebaseMessaging.send(message);
                log.info(" Successfully sent message: " + result);

                refreshToken(token);

                // 팝업 알림 등록(리스트)
                for (Popup popup : popupList) {
                    // 파라미터 EPushInfo에 따라 재오픈, 인기 토픽 지정
                    EPopupTopic topic = (info.equals(EPushInfo.REOPEN)) ? EPopupTopic.REOPEN : EPopupTopic.HOT;

                    PopupAlarmDto popupAlarmDto = PopupAlarmDto.fromEntity(
                            popup,
                            user,
                            info.getTitle(),
                            info.getBody(),
                            topic
                    );
                    alarmCommandUseCase.insertPopupAlarm(popupAlarmDto); // user, popup 추가 해야 함
                }

            } catch (FirebaseMessagingException e) {
                log.error("Failed to send message: " + e.getMessage());
            }
        }
    }

    @Override
    public void sendChoochunAlarm(User user, Popup popup, Review review, EPushInfo info) {

        Long userId = user.getId();
        int badge = alarmListQueryUseCase.countUnreadAlarms(userId);

        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(info.getTitle())
                        .setBody("[" + review.getPopup().getName() + "] " + info.getBody())
                        .build())
                .setApnsConfig(apnsConfiguration.apnsConfig(badge))
                .setAndroidConfig(androidConfiguration.androidConfig())
                .setToken(review.getToken())
                .putData("id", review.getPopup().getId().toString())
                .putData("type", "popup")
                .build();

        try {
            String result = firebaseMessaging.send(message);
            log.info("Successfully sent message: " + result);

            FCMToken token = tokenQueryUseCase.findByToken(review.getToken());
            refreshToken(token);

        } catch (FirebaseMessagingException e) {
            log.error("Failed to send message: " + e.getMessage());
        }

        PopupAlarmDto popupAlarmDto = PopupAlarmDto.fromEntity(
                popup,
                user,
                info.getTitle(),
                info.getBody(),
                EPopupTopic.CHOOCHUN
        );

        alarmCommandUseCase.insertPopupAlarm(popupAlarmDto);
    }

    @Override
    public void sendKeywordAlarm(FCMToken token, AlarmKeywordCreateRequestDto requestDto, UserAlarmKeyword userAlarmKeyword) {
        log.info("token : " + token.getToken());

        User user = tokenQueryUseCase.findUserByToken(token.getToken());
        Long userId = user.getId();

        int badge = alarmListQueryUseCase.countUnreadAlarms(userId);
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(requestDto.title())
                        .setBody(requestDto.body())
                        .build())
                .setApnsConfig(apnsConfiguration.apnsConfig(badge))
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

    @Transactional
    @Override
    public void sendPopupTopicAlarm(List<FCMRequestDto> fcmRequestDtoList) {
        for (FCMRequestDto fcmRequestDto : fcmRequestDtoList) {

            User user = tokenQueryUseCase.findUserByToken(fcmRequestDto.token());
            Long userId = user.getId();

            int badge = alarmListQueryUseCase.countUnreadAlarms(userId);

            // 팝업 정보 조회
            Popup popup = popupRepository.findById(Long.valueOf(fcmRequestDto.popupId()))
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

            // EPopupTopic과 EPushInfo 매핑
            EPopupTopic topic = fcmRequestDto.topic();
            EPushInfo info = findMatchingInfo(topic);

            // 알림 메시지 생성
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(info.getTitle())
                            .setBody("[" + popup.getName() + "] " + info.getBody())
                            .build())
                    .setTopic(topic.name())
                    .setAndroidConfig(androidConfiguration.androidConfig())
                    .setApnsConfig(apnsConfiguration.apnsConfig(badge))
                    .putData("id", String.valueOf(fcmRequestDto.popupId()))
                    .putData("type", "popup")
                    .build();

            log.info("TOPIC message sending...");
            try {
                String result = firebaseMessaging.send(message);
                log.info("Successfully sent message: " + result);

                // 토큰 갱신
                FCMToken token = tokenQueryUseCase.findByToken(fcmRequestDto.token());
                refreshToken(token);

                // 팝업 알림 저장
                PopupAlarmDto popupAlarmDto = PopupAlarmDto.fromEntity(
                        popup,
                        user,
                        info.getTitle(),
                        info.getBody(),
                        topic
                );
                alarmCommandUseCase.insertPopupAlarm(popupAlarmDto);

            } catch (FirebaseMessagingException e) {
                log.error("Failed to send message: " + e.getMessage());
            }
        }
    }

    /**
     * EPopupTopic과 EPushInfo를 매핑
     */
    private EPushInfo findMatchingInfo(EPopupTopic topic) {
        return Arrays.stream(EPushInfo.values())
                .filter(info -> info.name().equalsIgnoreCase(topic.name()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No matching EPushInfo for topic: " + topic.name()));
    }

}
