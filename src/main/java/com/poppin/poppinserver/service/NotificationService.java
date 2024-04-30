package com.poppin.poppinserver.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.domain.NotificationToken;
import com.poppin.poppinserver.domain.NotificationTopic;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.dto.notification.request.TokenRequestDto;
import com.poppin.poppinserver.dto.notification.response.TokenResponseDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.NotificationTokenRepository;

import com.poppin.poppinserver.repository.NotificationTopicRepository;
import com.poppin.poppinserver.type.ETopic;
import com.poppin.poppinserver.type.ETopicType;
import com.poppin.poppinserver.util.NotificationUtil;
import com.poppin.poppinserver.util.SubscribeUtil;
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
    private final NotificationTopicRepository notificationTopicRepository;

    private final NotificationUtil notificationUtil;
    private final SubscribeUtil subscribeUtil;
    /* 알림 동의 */
    public TokenResponseDto fcmApplyToken(TokenRequestDto tokenRequestDto){
        // 토큰 저장
        NotificationToken notificationToken = new NotificationToken(
                tokenRequestDto.token(),
                LocalDateTime.now(), // 토큰 등록 시간 + 토큰 만기 시간(+2달)
                tokenRequestDto.device() // android or ios
        );

        notificationTokenRepository.save(notificationToken);

        for (ETopic topic : ETopic.values()){
            if (topic.equals(ETopic.EVT) || topic.equals(ETopic.ERROR) || topic.equals(ETopic.NOTI)){ // 공지사항 ETopic 시에만
                NotificationTopic notificationTopic = new NotificationTopic(notificationToken,ETopicType.NT,LocalDateTime.now(),topic);
                notificationTopicRepository.save(notificationTopic); // 구독 저장
                fcmAddTopic(tokenRequestDto.token(), ETopicType.NT); // 구독
            }
        }


        return TokenResponseDto.fromEntity(tokenRequestDto);
    }

    /*
      Method : 관심 팝업 등록 시 주제 테이블에 데이터 삽입 , 구독 시키기
      Author : sakang
      Date   : 2024-04-27
    */
    public void fcmAddTopic(String token, ETopicType type){

        try {
            log.info("==== subscribe topic START ====");

            NotificationToken tk = notificationTokenRepository.findByToken(token);
            if (tk == null)throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);
            if (tk.getDevice().equals("android")){
                // 안드로이드
                if (type.equals(ETopicType.IP))subscribeUtil.androidSubscribeInterestedPopupTopic(tk); // 관심팝업
                if (type.equals(ETopicType.RO))subscribeUtil.androidSubscribeReopenPopupTopic(tk); // 재오픈팝업
                if (type.equals(ETopicType.NT))subscribeUtil.androidSubscribeNotificationTopic(tk); // 공지사항(공지, 이벤트, 에러)
            }else{
                // 아이폰
            }
        }catch (CommonException | FirebaseMessagingException e){
            log.error("==== subscribe topic FAILED ====");
            e.printStackTrace();
        }

    }

    public void fcmRemoveTokenFromTopic(String token, ETopicType type){

        try {
            log.info("==== unsubscribe topic START ====");

            NotificationToken tk = notificationTokenRepository.findByToken(token);
            if (tk == null)throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);
            if (tk.getDevice().equals("android")){
                // 안드로이드
                if (type.equals(ETopicType.IP))subscribeUtil.androidUnsubscribeInterestedPopupTopic(tk); // 구독 및 저장
            }else {
                // 아이폰
            }

        }catch (CommonException | FirebaseMessagingException e){
            log.error("==== unsubscribe topic FAILED ====");
            e.printStackTrace();
        }

    }


    
}
