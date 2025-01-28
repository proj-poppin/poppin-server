package com.poppin.poppinserver.alarm.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.domain.PopupTopic;
import com.poppin.poppinserver.alarm.repository.FCMTokenRepository;
import com.poppin.poppinserver.alarm.repository.PopupTopicRepository;
import com.poppin.poppinserver.alarm.usecase.TopicCommandUseCase;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm")
public class FCMController {
    //TODO: 삭제 예정
    private final FCMTokenRepository fcmTokenRepository;
    private final PopupTopicRepository popupTopicRepository;
    private final TopicCommandUseCase topicCommandUseCase;


    // 데이터베이스 초기화 시 토큰 팝업 구독 해제
    @PostMapping("/reset/topic")
    public ResponseDto<?> resetPopupTopic() throws FirebaseMessagingException {
        List<PopupTopic> topics = popupTopicRepository.findAll();
        for (PopupTopic topic : topics) {
            User user = topic.getUser();
            FCMToken token = fcmTokenRepository.findByUser(user);
            topicCommandUseCase.unsubscribePopupTopic(user, token, topic.getPopup(), EPopupTopic.MAGAM);
            topicCommandUseCase.unsubscribePopupTopic(user, token, topic.getPopup(), EPopupTopic.OPEN);
            topicCommandUseCase.unsubscribePopupTopic(user, token, topic.getPopup(), EPopupTopic.CHANGE_INFO);
        }
        return ResponseDto.ok("finish");
    }
}
