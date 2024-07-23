package com.poppin.poppinserver.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.fcm.request.ApplyTokenRequestDto;
import com.poppin.poppinserver.dto.fcm.response.ApplyTokenResponseDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.*;
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
    private final ReviewRepository reviewRepository;
    private final FCMSubscribeService fcmSubscribeService;
    private final PopupRepository popupRepository;

    /* FCM TOKEN 등록 */
    public ApplyTokenResponseDto fcmApplyToken(ApplyTokenRequestDto requestDto){

        log.info("applying token");
        log.info("apply token : {}" , requestDto.fcmToken());

        try {
            // 토큰 저장 여부 확인
            Optional<FCMToken> fcmTokenOptional = fcmTokenRepository.findByDeviceId(requestDto.deviceId());
            Boolean isDuplicate = fcmTokenOptional.isPresent();

            // 디바이스 ID와 토큰이 모두 동일한 경우
            if (isDuplicate && requestDto.fcmToken().equals(fcmTokenOptional.get().getToken())) {
                AlarmSetting alarmSetting = alarmSettingRepository.findByToken(requestDto.fcmToken());
                if (alarmSetting == null) {
                    alarmSetting = new AlarmSetting(requestDto.fcmToken(), "1", "1", "1", "1", "1", "1");
                    alarmSettingRepository.save(alarmSetting);
                    return ApplyTokenResponseDto.fromEntity(requestDto, "create alarm setting." , "알람 세팅이 생성되었습니다.");
                }
                else {
                    return ApplyTokenResponseDto.fromEntity(requestDto, "already exist alarm setting." , "기존 알람 세팅이 존재합니다.");
                }
            }
            else if (isDuplicate && !requestDto.fcmToken().equals(fcmTokenOptional.get().getToken())) { // 디바이스 ID는 동일하지만 토큰이 다른 경우
                // fcm token refreshing
                fcmTokenOptional.get().setToken(requestDto.fcmToken());
                fcmTokenOptional.get().regenerateToken();
                fcmTokenRepository.save(fcmTokenOptional.get());

                // review token refreshing
                List<Review> reviews = reviewRepository.findAllByToken(fcmTokenOptional.get().getToken());
                for (Review review : reviews){
                    review.setToken(requestDto.fcmToken());
                    reviewRepository.save(review);
                }

                return ApplyTokenResponseDto.fromEntity(requestDto, "duplicated device id. update token." , "토큰 업데이트.");
            } else {
                // 토큰 저장
                FCMToken FCMToken = new FCMToken(
                        requestDto.fcmToken(),
                        LocalDateTime.now(), // 토큰 등록 시간 + 토큰 만기 시간(+1달)
                        requestDto.device(), // android or ios
                        requestDto.deviceId()
                );

                fcmTokenRepository.save(FCMToken); // 토큰 저장
                return ApplyTokenResponseDto.fromEntity(requestDto, "fcm token save succeed" , "토큰이 저장되었습니다.");
            }
        } catch (Exception e){
            log.error("applying token failed {}", e.getMessage());
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

    public void fcmAddPopupTopic(String token, Popup popup, EPopupTopic topic){
        if(token != null){
            // 팝업 관련
            try {
                log.info("subscribe popup topic");

                FCMToken pushToken = fcmTokenRepository.findByToken(token);
                if (pushToken == null)throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);
                fcmSubscribeService.subscribePopupTopic(pushToken, popup , topic); // 관심팝업
            }catch (CommonException | FirebaseMessagingException e){
                log.error("failed to subscribe popup topic");
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
            log.info("unsubscribe popup topic");
            FCMToken pushToken = fcmTokenRepository.findByToken(token);
            if (pushToken == null)throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);
            fcmSubscribeService.unsubscribePopupTopic(pushToken, popup, topic); // 구독 및 저장
        }catch (CommonException | FirebaseMessagingException e){
            log.error("failed to unsubscribe popup topic");
            e.printStackTrace();
        }
    }


    public String resetPopupTopic(){

        List<FCMToken> fcmTokenList = fcmTokenRepository.findAll();
        for (FCMToken token : fcmTokenList){
            Optional<List<PopupTopic>> topics = popupTopicRepository.findByToken(token);
            if (topics.isPresent()){
                for (PopupTopic topic : topics.get()){
                    fcmRemovePopupTopic(token.getToken(),topic.getPopup(),EPopupTopic.MAGAM);
                    fcmRemovePopupTopic(token.getToken(),topic.getPopup(),EPopupTopic.OPEN);
                    fcmRemovePopupTopic(token.getToken(),topic.getPopup(),EPopupTopic.CHANGE_INFO);
                }
            }
        }
        return "finish";
    }

}

