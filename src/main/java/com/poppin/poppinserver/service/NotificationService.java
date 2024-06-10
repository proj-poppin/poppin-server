package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.InformationTopic;
import com.poppin.poppinserver.domain.NotificationToken;
import com.poppin.poppinserver.dto.notification.request.TokenRequestDto;
import com.poppin.poppinserver.dto.notification.response.TokenResponseDto;
import com.poppin.poppinserver.repository.InformationTopicRepository;
import com.poppin.poppinserver.repository.NotificationTokenRepository;

import com.poppin.poppinserver.type.EInformationTopic;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationTokenRepository notificationTokenRepository;
    private final InformationTopicRepository informationTopicRepository;

    private final FCMService fcmService;

    /* 알림 동의 - 공지사항 , 팝업은 따로 저장 해야 함 */
    public TokenResponseDto fcmApplyToken(TokenRequestDto tokenRequestDto){

        // 토큰 저장
        NotificationToken notificationToken = new NotificationToken(
                tokenRequestDto.token(),
                LocalDateTime.now(), // 토큰 등록 시간 + 토큰 만기 시간(+2달)
                tokenRequestDto.device() // android or ios
        );

        notificationTokenRepository.save(notificationToken); // 토큰 저장

        for (EInformationTopic topic : EInformationTopic.values()){
            InformationTopic popupTopic = new InformationTopic(notificationToken,topic.getTopicType(),LocalDateTime.now(),topic);
            informationTopicRepository.save(popupTopic); // 구독 저장
            fcmService.fcmAddTopic(tokenRequestDto.token(), null,  topic.getTopicType()); // 구독
        }

        return TokenResponseDto.fromEntity(tokenRequestDto);
    }

}
