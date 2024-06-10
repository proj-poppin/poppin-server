package com.poppin.poppinserver.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.domain.NotificationToken;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.NotificationTokenRepository;
import com.poppin.poppinserver.type.EPopupTopic;
import com.poppin.poppinserver.util.push.android.FCMSubscribeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FCMService {

    private final NotificationTokenRepository notificationTokenRepository;
    private final FCMSubscribeUtil fcmSubscribeUtil ;
    /*
          Method : 관심 팝업 등록 시 주제 테이블에 데이터 삽입 , 구독 시키기
          Author : sakang
          Date   : 2024-04-27
        */
    public void fcmAddTopic(String token, Popup popup, EPopupTopic topic){
        if(token != null){
            // 팝업 관련
            try {
                log.info("앱푸시 팝업 주제 추가 시작");

                NotificationToken pushToken = notificationTokenRepository.findByToken(token);
                if (pushToken == null)throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);
                fcmSubscribeUtil.subscribePopupTopic(pushToken, popup , topic); // 관심팝업
            }catch (CommonException | FirebaseMessagingException e){
                log.error("앱푸시 팝업 주제 추가 실패");
                e.printStackTrace();
            }
        }else{
            log.error("토큰이 존재하지 않아 앱푸시 팝업 주제 추가하지 않습니다");
        }
    }

    /*
          Method : 관심 팝업 삭제 시 주제 테이블에 구독 해제
          Author : sakang
          Date   : 2024-04-27
        */
    public void fcmRemoveTopic(String token, Popup popup, EPopupTopic topic){

        try {
            log.info("앱푸시 팝업 주제 삭제 시작");
            NotificationToken pushToken = notificationTokenRepository.findByToken(token);
            if (pushToken == null)throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);
            fcmSubscribeUtil.unsubscribePopupTopic(pushToken, popup, topic); // 구독 및 저장
        }catch (CommonException | FirebaseMessagingException e){
            log.error("앱푸시 팝업 주제 삭제 실패");
            e.printStackTrace();
        }
    }

}
