package com.poppin.poppinserver.alarm.service;

import com.poppin.poppinserver.alarm.domain.*;
import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmPopupRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmTokenRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.request.InformDetailDto;
import com.poppin.poppinserver.alarm.dto.alarm.response.InformAlarmListResponseDto;
import com.poppin.poppinserver.alarm.dto.alarm.response.InformAlarmResponseDto;
import com.poppin.poppinserver.alarm.dto.alarm.response.PopupAlarmResponseDto;
import com.poppin.poppinserver.alarm.dto.alarm.response.UnreadAlarmsResponseDto;
import com.poppin.poppinserver.alarm.repository.*;
import com.poppin.poppinserver.popup.dto.popup.response.PopupDetailDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupGuestDetailDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.popup.service.PopupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.poppin.poppinserver.core.util.FCMRefreshUtil.refreshToken;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmListService {
    private final InformAlarmImageRepository informAlarmImageRepository;
    private final FCMTokenRepository fcmTokenRepository;
    private final InformIsReadRepository informIsReadRepository;
    private final PopupAlarmRepository popupAlarmRepository;
    private final InformAlarmRepository informAlarmRepository;


    private final PopupService popupService;



    // 알림 - 팝업 공지사항(1 depth)
    public List<PopupAlarmResponseDto> readPopupAlarmList(AlarmTokenRequestDto fcmRequestDto){


        log.info("fcm token : {} " , fcmRequestDto.fcmToken());
        List<PopupAlarmResponseDto> popupAlarmResponseDtoList = new ArrayList<>();
        List<PopupAlarm> alarmList = popupAlarmRepository.findByKeywordOrderByIdDesc(fcmRequestDto.fcmToken());

        for (PopupAlarm alarm : alarmList){
            log.info("alarmList : {} " , alarmList);
            PopupAlarmResponseDto popupAlarmResponseDto = PopupAlarmResponseDto.fromEntity(alarm);
            popupAlarmResponseDtoList.add(popupAlarmResponseDto);
        }
        log.info("result : {} " , popupAlarmResponseDtoList);
        return popupAlarmResponseDtoList;
    }

    // 알림 - 로그인 팝업 공지사항(2 depth)
    public PopupDetailDto readPopupDetail(Long userId, AlarmPopupRequestDto requestDto){

        log.info("alarm popup login detail ...");

        Long alarmId = requestDto.alarmId();
        Long popupId = requestDto.popupId();
        String fcmToken = requestDto.fcmToken();

        log.info("alarm id : {}" , alarmId);
        log.info("popup id : {}" , popupId);
        log.info("fcm token : {}" , fcmToken);

        // 팝업 알림 isRead true 반환
        PopupAlarm popupAlarm = popupAlarmRepository.findById(alarmId)
                .orElseThrow(()-> new CommonException(ErrorCode.NOT_FOUND_POPUP_ALARM));
        popupAlarm.markAsRead();
        popupAlarmRepository.save(popupAlarm);

        // fcm token refresh
        FCMToken token = fcmTokenRepository.findByToken(fcmToken);
        refreshToken(token);

        // 팝업 상세 load
        PopupDetailDto popupDetailDto = popupService.readDetail(requestDto.popupId(), userId);

        return  popupDetailDto;

    }

    // 알림 - 비 로그인 팝업 공지사항(2 depth)
    public PopupGuestDetailDto readPopupDetailGuest(AlarmPopupRequestDto requestDto){

        log.info("alarm popup un-login detail ...");

        Long alarmId = requestDto.alarmId();
        Long popupId = requestDto.popupId();
        String fcmToken = requestDto.fcmToken();

        log.info("alarm id : {} " , alarmId);
        log.info("popup id : {} " , popupId);
        log.info("fcm token : {} " , fcmToken);


        // 팝업 알림 isRead true 반환
        PopupAlarm popupAlarm = popupAlarmRepository.findById(alarmId)
                .orElseThrow(()-> new CommonException(ErrorCode.NOT_FOUND_POPUP_ALARM));
        popupAlarm.markAsRead();
        popupAlarmRepository.save(popupAlarm);

        // fcm token refresh
        FCMToken token = fcmTokenRepository.findByToken(fcmToken);
        refreshToken(token);

        // 팝업 상세 정보
        PopupGuestDetailDto popupDetailDto = popupService.readGuestDetail(requestDto.popupId());

        return  popupDetailDto;
    }


    // 공지사항 알림 (1 depth)
    public List<InformAlarmListResponseDto> readInformAlarmList(AlarmTokenRequestDto requestDto){

        log.info("read inform alarm ...");

        log.info("fcm token : {} ", requestDto.fcmToken());

        List<InformAlarmListResponseDto> informAlarmListResponseDtoList = new ArrayList<>();
        List<InformAlarm> alarmList = informAlarmRepository.findByKeywordOrderByIdDesc(requestDto.fcmToken());

        log.info("alarm list : {}", alarmList);

        for (InformAlarm alarm : alarmList){
            InformIsRead informIsRead = informIsReadRepository.findByFcmTokenAndInformAlarm(requestDto.fcmToken(), alarm.getId());
            InformAlarmListResponseDto informAlarmListResponseDto = InformAlarmListResponseDto.fromEntity(alarm, informIsRead.getIsRead());
            informAlarmListResponseDtoList.add(informAlarmListResponseDto);
        }
        return informAlarmListResponseDtoList;
    }


    // 공지사항 알림 (2 depth)
    public InformAlarmResponseDto readInformDetail(InformDetailDto requestDto){

        String fcmToken = requestDto.fcmToken();
        Long informId = requestDto.informId();

        log.info("dto : {}" , requestDto);
        log.info("fcmToken : {}", fcmToken );
        log.info("inform ID : {}", informId);

        // isRead
        InformIsRead informIsRead = informIsReadRepository.findByFcmTokenAndInformAlarm(fcmToken,informId);
        log.info("inform : {}", informIsRead);

        informIsRead.markAsRead();
        informIsReadRepository.save(informIsRead);

        // informAlarm
        InformAlarm informAlarm = informAlarmRepository.findById(informId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_INFO_ALARM));

        // informAlarm img
        Optional<InformAlarmImage> img = informAlarmImageRepository.findByAlarmId(informId);
        if (img.isEmpty()) throw new CommonException(ErrorCode.NOT_FOUND_INFO_IMG);


            // InformAlarmResponseDto 객체 만들기
        else{
            InformAlarmResponseDto informAlarmResponseDto = InformAlarmResponseDto.builder()
                    .id(informAlarm.getId())
                    .title(informAlarm.getTitle())
                    .body(informAlarm.getBody())
                    .posterUrl(img.get().getPosterUrl())
                    .createdAt(informAlarm.getCreatedAt())
                    .build();
            return  informAlarmResponseDto;
        }
    }


    // 공지사항 읽음 여부 테이블에 유저 정보와 함께 저장
    public void insertInformIsRead(FCMToken token, InformAlarm informAlarm){
        InformIsRead informIsRead = new InformIsRead(informAlarm, token);
        informIsReadRepository.save(informIsRead);
    }

    public UnreadAlarmsResponseDto countUnreadAlarms(String fcmToken){

        int resultCount;

        int unreadInformAlarms = informIsReadRepository.unreadInforms(fcmToken);
        int unreadPopupAlarms = popupAlarmRepository.UnreadPopupAlarms(fcmToken);

        resultCount = unreadInformAlarms + unreadPopupAlarms;

        UnreadAlarmsResponseDto responseDto = UnreadAlarmsResponseDto.fromEntity(resultCount);

        return responseDto;
    }
}
