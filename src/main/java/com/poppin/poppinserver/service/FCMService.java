package com.poppin.poppinserver.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.domain.NotificationToken;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.NotificationTokenRepository;
import com.poppin.poppinserver.type.EInformationTopic;
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
    public void fcmAddTopic(String token, Popup popup, String type){
        if(popup != null){
            // 팝업 관련
            try {
                log.info("==== subscribe topic START ====");

                NotificationToken tk = notificationTokenRepository.findByToken(token);
                if (tk == null)throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);
                if (tk.getDevice().equals("android")){
                    // 안드로이드
                    fcmSubscribeUtil.androidSubscribePopupTopic(tk, popup , type); // 관심팝업
                }
            }catch (CommonException | FirebaseMessagingException e){
                log.error("==== subscribe topic FAILED ====");
                e.printStackTrace();
            }
        }
        else{
            // 공지사항 관련
            try {
                log.info("==== subscribe topic START ====");

                NotificationToken tk = notificationTokenRepository.findByToken(token);
                if (tk == null)throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);
                if (tk.getDevice().equals("android")){
                    // 안드로이드
                    for (EInformationTopic x : EInformationTopic.values() ){
                        if (x.getTopicType().equals(type)) fcmSubscribeUtil.androidSubscribeNotificationTopic(tk, popup);
                    } // 공지사항(공지, 이벤트, 에러)
                }
            }catch (CommonException | FirebaseMessagingException e){
                log.error("==== subscribe topic FAILED ====");
                e.printStackTrace();
            }
        }
    }

    /*
          Method : 관심 팝업 삭제 시 주제 테이블에 구독 해제
          Author : sakang
          Date   : 2024-04-27
        */
    public void fcmRemoveTopic(String token, String type){

        try {
            log.info("==== unsubscribe topic START ====");

            NotificationToken tk = notificationTokenRepository.findByToken(token);
            if (tk == null)throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);
            if (tk.getDevice().equals("android")){
                // 안드로이드
                fcmSubscribeUtil.androidUnsubscribePopupTopic(tk, type); // 구독 및 저장
            }
        }catch (CommonException | FirebaseMessagingException e){
            log.error("==== unsubscribe topic FAILED ====");
            e.printStackTrace();
        }

    }

}
