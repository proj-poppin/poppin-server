package com.poppin.poppinserver.util;

import com.google.firebase.messaging.*;


import com.poppin.poppinserver.config.APNsConfiguration;
import com.poppin.poppinserver.config.AndroidConfiguration;
import com.poppin.poppinserver.domain.InformAlarm;
import com.poppin.poppinserver.domain.NotificationToken;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.dto.alarm.request.InformAlarmRequestDto;
import com.poppin.poppinserver.dto.notification.request.FCMRequestDto;

import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.PopupRepository;
import com.poppin.poppinserver.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FCMSendUtil {

    private final PopupRepository popupRepository;
    private final FirebaseMessaging firebaseMessaging;
    private final APNsConfiguration apnsConfiguration;
    private final AndroidConfiguration androidConfiguration;

    private final AlarmService alarmService;



    /* 토큰 메시지 발송 */
    public String sendFCMToken(List<NotificationToken> tokenList, InformAlarmRequestDto requestDto, InformAlarm informAlarm) {
        try {
            for (NotificationToken token : tokenList) {
                log.info("token : " + token.getToken());
                Message message = Message.builder()
                        .setNotification(Notification.builder()
                                .setTitle(requestDto.title())
                                .setBody(requestDto.body())
                                .build())
                        .setApnsConfig(apnsConfiguration.apnsConfig())
                        .setAndroidConfig(androidConfiguration.androidConfig())
                        .setToken(token.getToken())
                        .putData("informId", informAlarm.getId().toString())
                        .build();

                try {
                    String result = firebaseMessaging.send(message);
                    log.info(" Successfully sent message: " + result);

                } catch (FirebaseMessagingException e) {
                    log.error("Failed to send message: " + e.getMessage());
                }
            }
            return "1";
        }catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }


    /**
     * 안드로이드 FCM Topic 앱 푸시 알림 메서드
     * @param fcmRequestDtoList FCM 주제 발송 객체
     * @throws FirebaseMessagingException FCM 오류
     */
    public void sendFCMTopicMessage(List<FCMRequestDto> fcmRequestDtoList){
        // aos
        AndroidConfig androidConfig = androidConfiguration.androidConfig();
        // ios
        ApnsConfig apnsConfig = apnsConfiguration.apnsConfig();

        for (FCMRequestDto fcmRequestDto : fcmRequestDtoList){

            Message message = null;

            Popup popup = popupRepository.findById(fcmRequestDto.popupId())
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

            message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(fcmRequestDto.title())
                            .setBody( popup.getName() + " "  + fcmRequestDto.body())
                            .build())
                    .setTopic(String.valueOf(fcmRequestDto.topic()))
                    .setAndroidConfig(androidConfig)
                    .setApnsConfig(apnsConfig)
                    .putData("popupId" , fcmRequestDto.popupId().toString())
                    .build();

            log.info("TOPIC message sending \n");
            try {

                String result = firebaseMessaging.send(message);
                log.debug( " Successfully sent message: " + result);

                // 알림 키워드 등록
                String flag = alarmService.insertPopupAlarmKeyword(fcmRequestDto);
                if (flag.equals("1")){
                    log.info(fcmRequestDto.token()+" alarm success ");
                }else{
                    log.error(fcmRequestDto.token() + " alarm fail");
                }
            } catch (FirebaseMessagingException e) {
                log.error(" Failed to send message: " + e.getMessage());
            }
        }

    }

    /**
     * 여러 기기 동시 전송
     * @param fcmRequestDtoList 멀티 FCM 발송 객체
     * @throws FirebaseMessagingException 발송 오류
     */
    public void sendMultiDeviceMessage(List<FCMRequestDto> fcmRequestDtoList)throws FirebaseMessagingException {
        List<String> tokenList = null;
        for (FCMRequestDto fcmRequestDto : fcmRequestDtoList){
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
        if (message != null){
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
            }else log.info(" List of tokens send messages SUCCESSFULLY");
        }
    }

}
