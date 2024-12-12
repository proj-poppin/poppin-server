package com.poppin.poppinserver.alarm.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.domain.PopupTopic;
import com.poppin.poppinserver.alarm.repository.FCMTokenRepository;
import com.poppin.poppinserver.alarm.usecase.TokenCommandUseCase;
import com.poppin.poppinserver.alarm.usecase.TokenQueryUseCase;
import com.poppin.poppinserver.alarm.usecase.TopicCommandUseCase;
import com.poppin.poppinserver.alarm.usecase.TopicQueryUseCase;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenCommandService implements TokenCommandUseCase {

    private final FCMTokenRepository fcmTokenRepository;
    //TODO: @조원준 userQueryRepository -> userQueryUseCase 적용 부탁드립니다.
    private final UserQueryRepository userQueryRepository;
    private final TokenQueryUseCase tokenQueryUseCase;
    private final TopicQueryUseCase topicQueryUseCase;
    private final TopicCommandUseCase topicCommandUseCase;


    @Override
    public void applyToken(String token, Long userId) {
        log.info("Applying FCM token: {}", token);

        // TODO: @조원준 userQueryRepository -> userQueryUseCase 적용 부탁드립니다.
        // 유저 조회
        User user = userQueryRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        tokenQueryUseCase.verifyToken(token);

        // FCM 토큰 저장
        FCMToken fcmTokenEntity = new FCMToken(user, token, LocalDateTime.now());
        fcmTokenRepository.save(fcmTokenEntity);
    }

    @Override
    public void verifyToken(Long userId, String token) {
        log.info("verify token : {}", token);

        Optional<FCMToken> fcmTokenOptional = fcmTokenRepository.findByUserId(Long.valueOf(userId));
        if (fcmTokenOptional.isPresent()) {
            String currentToken = fcmTokenOptional.get().getToken();
            if (!currentToken.equals(token)) {
                fcmTokenOptional.get().setToken(token);
                fcmTokenRepository.save(fcmTokenOptional.get());
                //TODO: 알림세팅 관련 로직이 필요함:
            }
        }
    }

    @Override
    public void removeToken(FCMToken token) throws FirebaseMessagingException {
        List<PopupTopic> topicsNeedToDelete = topicQueryUseCase.findPopupTopicByToken(token);
        if (!topicsNeedToDelete.isEmpty()) {
            for (PopupTopic topic : topicsNeedToDelete) {
                topicCommandUseCase.delete(topic);
                topicCommandUseCase.unsubscribePopupTopic(token,topic.getPopup(), EPopupTopic.MAGAM);
                topicCommandUseCase.unsubscribePopupTopic(token,topic.getPopup(), EPopupTopic.OPEN);
                topicCommandUseCase.unsubscribePopupTopic(token,topic.getPopup(), EPopupTopic.CHANGE_INFO);
            }
        }
        fcmTokenRepository.delete(token);
    }
}
