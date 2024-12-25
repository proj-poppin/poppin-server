//package com.poppin.poppinserver.legacy.alarm.service;
//
//import com.poppin.poppinserver.alarm.domain.FCMToken;
//import com.poppin.poppinserver.alarm.domain.PopupAlarm;
//import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmPopupRequestDto;
//import com.poppin.poppinserver.alarm.repository.FCMTokenRepository;
//import com.poppin.poppinserver.alarm.repository.InformAlarmImageRepository;
//import com.poppin.poppinserver.alarm.repository.InformAlarmRepository;
//import com.poppin.poppinserver.alarm.repository.InformIsReadRepository;
//import com.poppin.poppinserver.alarm.repository.PopupAlarmRepository;
//import com.poppin.poppinserver.core.exception.CommonException;
//import com.poppin.poppinserver.core.exception.ErrorCode;
//import com.poppin.poppinserver.popup.dto.popup.response.PopupDetailDto;
//import com.poppin.poppinserver.popup.dto.popup.response.PopupGuestDetailDto;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class DeprecatedAlarmListService {
//    private final FCMTokenRepository fcmTokenRepository;
//    private final PopupAlarmRepository popupAlarmRepository;
//
//    //TODO: 삭제 예정
//    // 알림 - 로그인 팝업 공지사항(2 depth)
//    public PopupDetailDto readPopupDetail(Long userId, AlarmPopupRequestDto requestDto) {
//
//        log.info("alarm popup login detail ...");
//
//        Long alarmId = requestDto.alarmId();
//        Long popupId = requestDto.popupId();
//        String fcmToken = requestDto.fcmToken();
//
//        log.info("alarm id : {}", alarmId);
//        log.info("popup id : {}", popupId);
//        log.info("fcm token : {}", fcmToken);
//
//        // 팝업 알림 isRead true 반환
//        PopupAlarm popupAlarm = popupAlarmRepository.findById(alarmId)
//                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP_ALARM));
//        popupAlarm.markAsRead();
//        popupAlarmRepository.save(popupAlarm);
//
//        // fcm token refresh
//        FCMToken token = fcmTokenRepository.findByToken(fcmToken);
//        refreshToken(token);
//
//        // 팝업 상세 load
//        PopupDetailDto popupDetailDto = popupService.readDetail(requestDto.popupId(), userId);
//
//        return popupDetailDto;
//
//    }
//
//    //TODO: 삭제 예정
//    // 알림 - 비 로그인 팝업 공지사항(2 depth)
//    public PopupGuestDetailDto readPopupDetailGuest(AlarmPopupRequestDto requestDto) {
//
//        log.info("alarm popup un-login detail ...");
//
//        Long alarmId = requestDto.alarmId();
//        Long popupId = requestDto.popupId();
//        String fcmToken = requestDto.fcmToken();
//
//        log.info("alarm id : {} ", alarmId);
//        log.info("popup id : {} ", popupId);
//        log.info("fcm token : {} ", fcmToken);
//
//        // 팝업 알림 isRead true 반환
//        PopupAlarm popupAlarm = popupAlarmRepository.findById(alarmId)
//                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP_ALARM));
//        popupAlarm.markAsRead();
//        popupAlarmRepository.save(popupAlarm);
//
//        // fcm token refresh
//        FCMToken token = fcmTokenRepository.findByToken(fcmToken);
//        refreshToken(token);
//
//        // 팝업 상세 정보
//        PopupGuestDetailDto popupDetailDto = popupService.readGuestDetail(requestDto.popupId());
//
//        return popupDetailDto;
//    }
//}
