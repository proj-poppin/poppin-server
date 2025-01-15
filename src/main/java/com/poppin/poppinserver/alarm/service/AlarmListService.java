package com.poppin.poppinserver.alarm.service;

import com.poppin.poppinserver.alarm.repository.InformAlarmImageRepository;
import com.poppin.poppinserver.alarm.repository.InformAlarmRepository;
import com.poppin.poppinserver.alarm.repository.PopupAlarmRepository;
import com.poppin.poppinserver.alarm.repository.UserInformAlarmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmListService {
    private final InformAlarmImageRepository informAlarmImageRepository;
    private final UserInformAlarmRepository userInformAlarmRepository;
    private final PopupAlarmRepository popupAlarmRepository;
    private final InformAlarmRepository informAlarmRepository;


    // 알림 - 팝업 공지사항(1 depth)
//    public List<PopupAlarmResponseDto> readPopupAlarmList(AlarmTokenRequestDto fcmRequestDto) {
//
//        log.info("fcm token : {} ", fcmRequestDto.fcmToken());
//        List<PopupAlarmResponseDto> popupAlarmResponseDtoList = new ArrayList<>();
//        List<PopupAlarm> alarmList = popupAlarmRepository.findByKeywordOrderByIdDesc(fcmRequestDto.fcmToken());
//
//        for (PopupAlarm alarm : alarmList) {
//            log.info("alarmList : {} ", alarmList);
//            PopupAlarmResponseDto popupAlarmResponseDto = PopupAlarmResponseDto.fromEntity(alarm);
//            popupAlarmResponseDtoList.add(popupAlarmResponseDto);
//        }
//        log.info("result : {} ", popupAlarmResponseDtoList);
//        return popupAlarmResponseDtoList;
//    }


//    // 공지사항 알림 (1 depth)
//    public List<InformAlarmListResponseDto> readInformAlarmList(AlarmTokenRequestDto requestDto) {
//
//        log.info("read inform alarm ...");
//
//        log.info("fcm token : {} ", requestDto.fcmToken());
//
//        List<InformAlarmListResponseDto> informAlarmListResponseDtoList = new ArrayList<>();
//        List<InformAlarm> alarmList = informAlarmRepository.findByKeywordOrderByIdDesc(requestDto.fcmToken());
//
//        log.info("alarm list : {}", alarmList);
//
//        for (InformAlarm alarm : alarmList) {
//            UserInformAlarm userInformAlarm = informIsReadRepository.findByFcmTokenAndInformAlarm(requestDto.fcmToken(),
//                    alarm.getId());
//            InformAlarmListResponseDto informAlarmListResponseDto = InformAlarmListResponseDto.fromEntity(alarm,
//                    userInformAlarm.getIsRead());
//            informAlarmListResponseDtoList.add(informAlarmListResponseDto);
//        }
//        return informAlarmListResponseDtoList;
//    }
//
//
//    // 공지사항 알림 (2 depth)
//    public InformAlarmResponseDto readInformDetail(InformDetailDto requestDto) {
//
//        String fcmToken = requestDto.fcmToken();
//        Long informId = Long.valueOf(requestDto.informId());
//
//        log.info("dto : {}", requestDto);
//        log.info("fcmToken : {}", fcmToken);
//        log.info("inform ID : {}", informId);
//
//        // isRead
//        UserInformAlarm userInformAlarm = informIsReadRepository.findByFcmTokenAndInformAlarm(fcmToken, informId);
//        log.info("inform : {}", userInformAlarm);
//
//        userInformAlarm.markAsRead();
//        informIsReadRepository.save(userInformAlarm);
//
//        // informAlarm
//        InformAlarm informAlarm = informAlarmRepository.findById(informId)
//                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_INFO_ALARM));
//
//        // informAlarm img
//        Optional<InformAlarmImage> img = informAlarmImageRepository.findByAlarmId(informId);
//        if (img.isEmpty()) {
//            throw new CommonException(ErrorCode.NOT_FOUND_INFO_IMG);
//        }
//
//        // InformAlarmResponseDto 객체 만들기
//        else {
//            InformAlarmResponseDto informAlarmResponseDto = InformAlarmResponseDto.builder()
//                    .id(String.valueOf(informAlarm.getId()))
//                    .title(informAlarm.getTitle())
//                    .body(informAlarm.getBody())
//                    .posterUrl(img.get().getPosterUrl())
//                    .createdAt(informAlarm.getCreatedAt())
//                    .build();
//            return informAlarmResponseDto;
//        }
//    }

}
