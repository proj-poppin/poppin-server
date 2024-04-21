package com.poppin.poppinserver.scheduler;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.poppin.poppinserver.dto.alarm.common.MessageType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Service
public class NotificationScheduler {

    @Value("${firebase.messaging.firebase-create-scoped}")
    String fireBaseCreateScoped;

    @Value("${firebase.messaging.test-topic}")
    String topic;

    @Value("${firebase.messaging.json}")
    String json;

    private FirebaseMessaging instance;

    @PostConstruct
    public void firebaseSetting() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ClassPathResource(json).getInputStream())
                .createScoped((Arrays.asList(fireBaseCreateScoped)));
        FirebaseOptions secondaryAppConfig = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .build();
        FirebaseApp app = FirebaseApp.initializeApp(secondaryAppConfig);
        this.instance = FirebaseMessaging.getInstance(app);
    }
    @Scheduled(cron = "0 0 09 * * ?")
    public void pushMorningDietAlarm() throws FirebaseMessagingException {
        log.info("매일 아침 알림 전달");
        pushAlarm(MessageType.TEST_ALARM);
    }

//    @Scheduled(cron = "0 0 13 * * ?")
//    public void pushLunchDietAlarm() throws FirebaseMessagingException {
//        log.info("점심 식사 알림");
//        pushAlarm(LUNCH_DIET);
//    }
//
//    @Scheduled(cron = "0 0 19 * * ?")
//    public void pushDinnerDietAlarm() throws FirebaseMessagingException {
//        log.info("저녁 식사 알림");
//        pushAlarm(DINNER_DIET);
//    }

    private void pushAlarm(MessageType data) throws FirebaseMessagingException {
        Message message = getMessage(data);
        sendMessage(message);
    }

    private Message getMessage(MessageType data) {
        Notification notification = Notification.builder().setTitle(data.getTitle()).setBody(data.getBody()).build();
        Message.Builder builder = Message.builder();
        Message message = builder.setTopic(topic).setNotification(notification).build();
        return message;
    }

    public String sendMessage(Message message) throws FirebaseMessagingException {
        return this.instance.send(message);
    }
}
