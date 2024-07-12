package com.poppin.poppinserver.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.domain.AlarmSetting;
import com.poppin.poppinserver.domain.FCMToken;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.dto.fcm.request.ApplyTokenRequestDto;
import com.poppin.poppinserver.dto.fcm.request.DuplicateTokenReqDto;
import com.poppin.poppinserver.dto.fcm.response.ApplyTokenResponseDto;
import com.poppin.poppinserver.dto.fcm.response.DuplicateTokenResDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.AlarmSettingRepository;
import com.poppin.poppinserver.repository.FCMTokenRepository;
import com.poppin.poppinserver.type.EPopupTopic;
import com.poppin.poppinserver.util.FCMSubscribeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FCMService {

    private final FCMTokenRepository fcmTokenRepository;
    private final AlarmSettingRepository alarmSettingRepository;
    private final FCMSubscribeUtil fcmSubscribeUtil ;

    /* FCM TOKEN 중복 검사 */
    public DuplicateTokenResDto isDuplicateFCMToken(DuplicateTokenReqDto reqDto){
        Optional<FCMToken> token = fcmTokenRepository.findByTokenOpt(reqDto.fcmToken());

        // 존재하면 true, 존재하지 않으면 false
        Boolean isDuplicate = token.isPresent();

        return DuplicateTokenResDto.fromEntity(isDuplicate);
    }

    /* FCM TOKEN 등록 */
    public ApplyTokenResponseDto FCMApplyToken(ApplyTokenRequestDto applyTokenRequestDto){

        try {

            // 토큰 저장 여부 확인
            FCMToken isToken = fcmTokenRepository.findByToken(applyTokenRequestDto.token());
            if (isToken!=null) throw new CommonException(ErrorCode.DUPLICATED_TOKEN);
            else{
                // 알림 전부 "1"로 저장
                AlarmSetting alarmSetting = new AlarmSetting(applyTokenRequestDto.token(), "1", "1", "1","1","1", "1");
                alarmSettingRepository.save(alarmSetting);

                // 토큰 저장
                FCMToken FCMToken = new FCMToken(
                        applyTokenRequestDto.token(),
                        LocalDateTime.now(), // 토큰 등록 시간 + 토큰 만기 시간(+2달)
                        applyTokenRequestDto.device() // android or ios
                );
                FCMToken token = fcmTokenRepository.save(FCMToken); // 토큰 저장
                return ApplyTokenResponseDto.fromEntity(applyTokenRequestDto, "token 저장 성공" , token.getToken());
            }
        }catch (Exception e){
            log.error("토큰 등록 실패: " + e.getMessage());
            return ApplyTokenResponseDto.fromEntity(applyTokenRequestDto, "token 저장 실패" , e.getMessage());
        }
    }



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

                FCMToken pushToken = fcmTokenRepository.findByToken(token);
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
            FCMToken pushToken = fcmTokenRepository.findByToken(token);
            if (pushToken == null)throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);
            fcmSubscribeUtil.unsubscribePopupTopic(pushToken, popup, topic); // 구독 및 저장
        }catch (CommonException | FirebaseMessagingException e){
            log.error("앱푸시 팝업 주제 삭제 실패");
            e.printStackTrace();
        }
    }

}
