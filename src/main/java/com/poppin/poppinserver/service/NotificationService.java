package com.poppin.poppinserver.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.domain.NotificationToken;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.dto.notification.request.TokenRequestDto;
import com.poppin.poppinserver.dto.notification.response.TokenResponseDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.NotificationTokenRepository;

import com.poppin.poppinserver.util.NotificationUtil;
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

    private final NotificationUtil notificationUtil;

    /* 알림 동의 */
    public TokenResponseDto fcmApplyToken(TokenRequestDto tokenRequestDto){

        NotificationToken notificationToken = new NotificationToken(
                tokenRequestDto.token(),
                LocalDateTime.now(), // 토큰 등록 시간 + 토큰 만기 시간(+2달)
                tokenRequestDto.device() // android or ios
        );

        notificationTokenRepository.save(notificationToken);

        return TokenResponseDto.fromEntity(tokenRequestDto);
    }

    /*
      Method : 관심 팝업 등록 시 주제 테이블에 데이터 삽입 , 구독 시키기
      Author : sakang
      Date   : 2024-04-27
    */
    public void fcmAddTopic(String token, Popup popup){

        try {
            log.info("==== subscribe topic START ====");

            NotificationToken tk = notificationTokenRepository.findByToken(token);
            if (tk == null)throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);
            if (tk.getDevice().equals("android")){
                // 안드로이드
                notificationUtil.androidSubscribeInterestedPopupTopic(tk,popup); // 구독 및 저장
            }else{
                // 아이폰
            }
        }catch (CommonException | FirebaseMessagingException e){
            log.error("==== subscribe topic FAILED ====");
            e.printStackTrace();
        }

    }

    public void fcmRemoveTokenFromTopic(String token, Popup popup){

        try {
            log.info("==== unsubscribe topic START ====");

            NotificationToken tk = notificationTokenRepository.findByToken(token);
            if (tk == null)throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);
            if (tk.getDevice().equals("android")){
                // 안드로이드
                notificationUtil.androidUnsubscribeInterestedPopupTopic(tk,popup); // 구독 및 저장
            }else {
                // 아이폰
            }

        }catch (CommonException | FirebaseMessagingException e){
            log.error("==== unsubscribe topic FAILED ====");
            e.printStackTrace();
        }

    }
    
}
