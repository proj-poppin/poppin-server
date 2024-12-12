package com.poppin.poppinserver.alarm.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.domain.PopupTopic;
import com.poppin.poppinserver.alarm.repository.FCMTokenRepository;
import com.poppin.poppinserver.alarm.usecase.TopicCommandUseCase;
import com.poppin.poppinserver.alarm.usecase.TopicQueryUseCase;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.core.type.EPopupTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/noti")
public class FCMController {

    //TODO: 삭제 예정
      private final FCMTokenRepository fcmTokenRepository;

      private final TopicQueryUseCase topicQueryUseCase;
      private final TopicCommandUseCase topicCommandUseCase;


    // 데이터베이스 초기화 시 토큰 팝업 구독 해제
    @PostMapping("/reset/topic")
    public ResponseDto<?> resetPopupTopic() throws FirebaseMessagingException {
        List<FCMToken> fcmTokenList = fcmTokenRepository.findAll();
        for (FCMToken token : fcmTokenList) {
            List<PopupTopic> topics = topicQueryUseCase.findPopupTopicByToken(token);
            if (!topics.isEmpty()) {
                for (PopupTopic topic : topics) {

                    topicCommandUseCase.unsubscribePopupTopic(token, topic.getPopup(), EPopupTopic.MAGAM);
                    topicCommandUseCase.unsubscribePopupTopic(token, topic.getPopup(), EPopupTopic.OPEN);
                    topicCommandUseCase.unsubscribePopupTopic(token, topic.getPopup(), EPopupTopic.CHANGE_INFO);
                }
            }
        }
        return ResponseDto.ok("finish");
    }
}
