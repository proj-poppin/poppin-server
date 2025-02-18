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
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.core.type.EPushInfo;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.popup.usecase.PopupQueryUseCase;
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

    private final PopupQueryUseCase popupQueryUseCase;
    private final TokenQueryUseCase tokenQueryUseCase;
    private final AlarmListQueryUseCase alarmListQueryUseCase;
    private final AlarmCommandUseCase alarmCommandUseCase;

    @Override
    public void sendInformationAlarm(List<User> userList, InformAlarmCreateRequestDto requestDto, InformAlarm informAlarm) {
        for (User user : userList) {

            FCMToken token = tokenQueryUseCase.findByUser(user);

            int badge = alarmListQueryUseCase.countUnreadAlarms(user.getId());

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
    public void sendScheduledPopupAlarm(List<Popup> popupList, EPushInfo info) { // 예시. <인기 팝업 id 리스트, 인기 팝업 알림 정보>
        List<Long> popupIdList = new ArrayList<>();
        for (Popup p : popupList) {
            popupIdList.add(p.getId());
        }

        List<FCMToken> tokenList = tokenQueryUseCase.findAll(); // 모든 토큰을 찾아서

        for (FCMToken token : tokenList) {

            User user = tokenQueryUseCase.findUserByToken(token); // 토큰으로 유저 정보를 찾는다 -> 문제. 유니크 아니면 여러 유저가 나온다.-> 에러
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
        FCMToken fcmToken = tokenQueryUseCase.findByUser(review.getUser());
        int badge = alarmListQueryUseCase.countUnreadAlarms(userId);

        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(info.getTitle())
                        .setBody("[" + review.getPopup().getName() + "] " + info.getBody())
                        .build())
                .setApnsConfig(apnsConfiguration.apnsConfig(badge))
                .setAndroidConfig(androidConfiguration.androidConfig())
                .setToken(fcmToken.getToken())
                .putData("id", review.getPopup().getId().toString())
                .putData("type", "popup")
                .build();

        try {
            String result = firebaseMessaging.send(message);
            log.info("Successfully sent message: " + result);

            FCMToken token = tokenQueryUseCase.findByUser(user);
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

        User user = tokenQueryUseCase.findUserByToken(token);
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

            FCMToken token = tokenQueryUseCase.findByToken(fcmRequestDto.token());
            if (token == null) {
                log.warn("토큰 정보 없음: {}", fcmRequestDto.token());
                continue; // 토큰이 없으면 알림을 보낼 수 없음
            }

            User user = tokenQueryUseCase.findUserByToken(token);
            Long userId = user.getId();

            int badge = alarmListQueryUseCase.countUnreadAlarms(userId);

            // 팝업 정보 조회
            Popup popup = popupQueryUseCase.findPopupByIdElseNull(Long.valueOf(fcmRequestDto.popupId()));
            if (popup == null) {
                log.error("존재하지 않는 팝업 ID: {}", fcmRequestDto.popupId());
                continue;
            }

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
