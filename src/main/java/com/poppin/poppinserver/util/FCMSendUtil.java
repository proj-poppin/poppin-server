package com.poppin.poppinserver.util;

import com.google.firebase.messaging.*;


import com.poppin.poppinserver.config.APNsConfiguration;
import com.poppin.poppinserver.config.AndroidConfiguration;
import com.poppin.poppinserver.domain.InformAlarm;
import com.poppin.poppinserver.domain.FCMToken;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.dto.alarm.request.InformAlarmCreateRequestDto;
import com.poppin.poppinserver.dto.fcm.request.FCMRequestDto;

import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.FCMTokenRepository;
import com.poppin.poppinserver.repository.PopupRepository;
import com.poppin.poppinserver.service.AlarmService;
import com.poppin.poppinserver.type.EPushInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.poppin.poppinserver.util.FCMTokenUtil.refreshToken;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FCMSendUtil {

    private final FirebaseMessaging firebaseMessaging;
    private final APNsConfiguration apnsConfiguration;
    private final AndroidConfiguration androidConfiguration;

    private final PopupRepository popupRepository;
    private final FCMTokenRepository fcmTokenRepository;

    private final AlarmService alarmService;


    /**
     * 공지사항 토큰 메시지 발송
     * @param tokenList
     * @param requestDto
     * @param informAlarm
     * @return
     */
    public String sendInformationByFCMToken(List<FCMToken> tokenList, InformAlarmCreateRequestDto requestDto, InformAlarm informAlarm) {

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
                    .putData("informId", informAlarm.getId().toString())
                    .build();

            try {
                String result = firebaseMessaging.send(message);
                log.info(" Successfully sent message: " + result);

                refreshToken(token); // 토큰일자 갱신

            } catch (FirebaseMessagingException e) {
                log.error("Failed to send message: " + e.getMessage());
                return "0"; // fail
            }
        }
        return "1"; // success
    }

    /**
     * 스케줄러 토큰 발송
     * @param popupList
     * @param info
     * @return
     */
    public String sendByFCMToken(List<Popup> popupList , EPushInfo info) {
        try {
            // 인기,
            List<Long> popupIdList = new ArrayList<>();
            for (Popup p : popupList){
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

            log.info("TOPIC message sending...");
            try {

                String result = firebaseMessaging.send(message);
                log.debug( "Successfully sent message: " + result);

                FCMToken token = fcmTokenRepository.findByToken(fcmRequestDto.token());
                refreshToken(token); // 토큰 갱신

                // 알림 키워드 등록
                String flag = alarmService.insertPopupAlarm(fcmRequestDto);
                if (flag.equals("1")){
                    log.info(fcmRequestDto.token() +    " alarm success");
                }else{
                    log.error(fcmRequestDto.token() +   " alarm fail");
                }
            } catch (FirebaseMessagingException e) {
                log.error("Failed to send message: " + e.getMessage());
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
