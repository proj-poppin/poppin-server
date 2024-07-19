package com.poppin.poppinserver.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.domain.AlarmSetting;
import com.poppin.poppinserver.domain.FCMToken;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.PopupTopic;
import com.poppin.poppinserver.dto.fcm.request.ApplyTokenRequestDto;
import com.poppin.poppinserver.dto.fcm.response.ApplyTokenResponseDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.AlarmSettingRepository;
import com.poppin.poppinserver.repository.FCMTokenRepository;
import com.poppin.poppinserver.repository.PopupTopicRepository;
import com.poppin.poppinserver.type.EPopupTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMTokenService {

    private final FCMTokenRepository fcmTokenRepository;

    private final PopupTopicRepository popupTopicRepository;
    private final AlarmSettingRepository alarmSettingRepository;
    private final FCMSubscribeService fcmSubscribeService;

    /* FCM TOKEN 등록 */
    public ApplyTokenResponseDto fcmApplyToken(ApplyTokenRequestDto requestDto){

        log.info("apply token : {}" , requestDto.fcmToken());
        try {

            // 토큰 저장 여부 확인
            Optional<FCMToken> fcmTokenOptional = fcmTokenRepository.findByTokenOpt(requestDto.fcmToken());

            // 존재하면 true, 존재하지 않으면 false
            Boolean isDuplicate = fcmTokenOptional.isPresent();

            if (isDuplicate){
                return ApplyTokenResponseDto.fromEntity(requestDto, "duplicated fcm token" , "토큰이 중복되어 저장하지 않습니다.");
            }
            else{
                // 알림 전부 "1"로 저장
                AlarmSetting alarmSetting = new AlarmSetting(requestDto.fcmToken(), "1", "1", "1","1","1", "1");
                alarmSettingRepository.save(alarmSetting);

                // 토큰 저장
                FCMToken FCMToken = new FCMToken(
                        requestDto.fcmToken(),
                        LocalDateTime.now(), // 토큰 등록 시간 + 토큰 만기 시간(+2달)
                        requestDto.device() // android or ios
                );

                fcmTokenRepository.save(FCMToken); // 토큰 저장
                return ApplyTokenResponseDto.fromEntity(requestDto, "fcm token save succeed" , "토큰이 저장되었습니다.");
            }
        }catch (Exception e){
            log.error("토큰 등록 실패: " + e.getMessage());
            return ApplyTokenResponseDto.fromEntity(requestDto, "fcm token save fail" , e.getMessage());
        }
    }

    public void fcmRemoveToken(FCMToken token) throws FirebaseMessagingException {

        // 구독내역 존재 시 전부 삭제
        Optional<List<PopupTopic>> topicsNeedToDelete = popupTopicRepository.findByToken(token);
        if (topicsNeedToDelete.isPresent()){
            for (PopupTopic topic : topicsNeedToDelete.get()) {
                popupTopicRepository.delete(topic);
                fcmSubscribeService.unsubscribePopupTopic(token, topic.getPopup(), EPopupTopic.MAGAM);
                fcmSubscribeService.unsubscribePopupTopic(token, topic.getPopup(), EPopupTopic.OPEN);
                fcmSubscribeService.unsubscribePopupTopic(token, topic.getPopup(), EPopupTopic.CHANGE_INFO);
            }
        }

        // 후기쪽 토큰 refresh
        // 토큰 삭제
        fcmTokenRepository.delete(token);

    }

    /*
          Method : 관심 팝업 등록 시 주제 테이블에 데이터 삽입 , 구독 시키기
          Author : sakang
          Date   : 2024-04-27
    */
    public void fcmAddPopupTopic(String token, Popup popup, EPopupTopic topic){
        if(token != null){
            // 팝업 관련
            try {
                log.info("앱푸시 팝업 주제 추가 시작");

                FCMToken pushToken = fcmTokenRepository.findByToken(token);
                if (pushToken == null)throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);
                fcmSubscribeService.subscribePopupTopic(pushToken, popup , topic); // 관심팝업
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
    public void fcmRemovePopupTopic(String token, Popup popup, EPopupTopic topic){

        try {
            log.info("앱푸시 팝업 주제 삭제 시작");
            FCMToken pushToken = fcmTokenRepository.findByToken(token);
            if (pushToken == null)throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);
            fcmSubscribeService.unsubscribePopupTopic(pushToken, popup, topic); // 구독 및 저장
        }catch (CommonException | FirebaseMessagingException e){
            log.error("앱푸시 팝업 주제 삭제 실패");
            e.printStackTrace();
        }
    }


}
