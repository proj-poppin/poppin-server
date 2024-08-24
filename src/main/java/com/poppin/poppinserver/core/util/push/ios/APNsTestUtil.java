package com.poppin.poppinserver.core.util.push.ios;

import com.poppin.poppinserver.alarm.dto.fcm.request.APNsRequestDto;

import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import javapns.notification.ResponsePacket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class APNsTestUtil {

    @Value("${apns.name}")
    private String FILE_NAME;

    @Value("${apns.password}")
    private String PASSWORD;


    //TEST CODE
    public void sendAPNsTokenTest(String token) throws Exception {

        try {
            PushNotificationPayload payload = PushNotificationPayload.complex();
            payload.addAlert("title");
            payload.getPayload().put("message", "body");
            payload.addBadge(1);
            payload.addSound("default");
            payload.addCustomDictionary("id", "1");
            System.out.println(payload.toString());
            Object obj = token;
            ClassPathResource resource = new ClassPathResource(FILE_NAME);
            List<PushedNotification> NOTIFICATIONS = Push.payload(payload, resource.getPath(), PASSWORD, false, obj);
            for (PushedNotification NOTIFICATION : NOTIFICATIONS) {
                if (NOTIFICATION.isSuccessful()) {
                    log.info("PUSH NOTIFICATION SENT SUCCESSFULLY TO" + NOTIFICATION.getDevice().getToken());
                } else {
                    //부적절한 토큰 DB에서 삭제하기
                    Exception PROBLEM = NOTIFICATION.getException();
                    PROBLEM.printStackTrace();
                    ResponsePacket ERROR_RESPONSE = NOTIFICATION.getResponse();
                    if (ERROR_RESPONSE != null) {
                        log.info(ERROR_RESPONSE.getMessage());
                    }
                }
            }
        } catch (KeystoreException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }


    // APP PUSH
    public void sendAPNsToken(List<APNsRequestDto> requestDto) throws Exception {
       for (APNsRequestDto user : requestDto){
            if (user.token() != null) {
                try {
                    PushNotificationPayload payload = PushNotificationPayload.complex();
                    payload.addAlert(user.title());
                    payload.getPayload().put("message", user.body());
                    payload.addBadge(1);
                    payload.addSound("default");
                    payload.addCustomDictionary("id", "1");
                    log.info("payload info : " + payload.toString());
                    Object obj = user.token();
                    ClassPathResource resource = new ClassPathResource(FILE_NAME);

                    List<PushedNotification> NOTIFICATIONS = Push.payload(payload, resource.getPath(), PASSWORD, user.testType(), obj);

                    for (PushedNotification NOTIFICATION : NOTIFICATIONS) {
                        if (NOTIFICATION.isSuccessful()) {
                            log.info("PUSH NOTIFICATION SENT SUCCESSFULLY TO" + NOTIFICATION.getDevice().getToken());
                        } else {
                            //부적절한 토큰 DB에서 삭제하기
                            Exception PROBLEM = NOTIFICATION.getException();
                            PROBLEM.printStackTrace();
                            ResponsePacket ERROR_RESPONSE = NOTIFICATION.getResponse();
                            if (ERROR_RESPONSE != null) {
                                log.info(ERROR_RESPONSE.getMessage());
                            }
                        }
                    }
                } catch (KeystoreException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                log.info("알림을 성공적으로 전송했습니다. targetUserID=" + user.token());
            } else {
                log.info("서버에 저장된 해당 유저의 FirebaseToken이 존재하지 않습니다. targetUserID=" + null);
            }
       }
    }
}
