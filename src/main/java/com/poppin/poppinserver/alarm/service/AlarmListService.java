package com.poppin.poppinserver.alarm.service;

import com.poppin.poppinserver.alarm.domain.InformAlarm;
import com.poppin.poppinserver.alarm.domain.InformAlarmImage;
import com.poppin.poppinserver.alarm.domain.InformIsRead;
import com.poppin.poppinserver.alarm.domain.PopupAlarm;
import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmTokenRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.request.InformDetailDto;
import com.poppin.poppinserver.alarm.dto.alarm.response.InformAlarmListResponseDto;
import com.poppin.poppinserver.alarm.dto.alarm.response.InformAlarmResponseDto;
import com.poppin.poppinserver.alarm.dto.alarm.response.PopupAlarmResponseDto;
import com.poppin.poppinserver.alarm.repository.InformAlarmImageRepository;
import com.poppin.poppinserver.alarm.repository.InformAlarmRepository;
import com.poppin.poppinserver.alarm.repository.InformIsReadRepository;
import com.poppin.poppinserver.alarm.repository.PopupAlarmRepository;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmListService {
    private final InformAlarmImageRepository informAlarmImageRepository;
    private final InformIsReadRepository informIsReadRepository;
    private final PopupAlarmRepository popupAlarmRepository;
    private final InformAlarmRepository informAlarmRepository;


    // 알림 - 팝업 공지사항(1 depth)
    public List<PopupAlarmResponseDto> readPopupAlarmList(AlarmTokenRequestDto fcmRequestDto) {

        log.info("fcm token : {} ", fcmRequestDto.fcmToken());
        List<PopupAlarmResponseDto> popupAlarmResponseDtoList = new ArrayList<>();
        List<PopupAlarm> alarmList = popupAlarmRepository.findByKeywordOrderByIdDesc(fcmRequestDto.fcmToken());

        for (PopupAlarm alarm : alarmList) {
            log.info("alarmList : {} ", alarmList);
            PopupAlarmResponseDto popupAlarmResponseDto = PopupAlarmResponseDto.fromEntity(alarm);
            popupAlarmResponseDtoList.add(popupAlarmResponseDto);
        }
        log.info("result : {} ", popupAlarmResponseDtoList);
        return popupAlarmResponseDtoList;
    }


    // 공지사항 알림 (1 depth)
    public List<InformAlarmListResponseDto> readInformAlarmList(AlarmTokenRequestDto requestDto) {

        log.info("read inform alarm ...");

        log.info("fcm token : {} ", requestDto.fcmToken());

        List<InformAlarmListResponseDto> informAlarmListResponseDtoList = new ArrayList<>();
        List<InformAlarm> alarmList = informAlarmRepository.findByKeywordOrderByIdDesc(requestDto.fcmToken());

        log.info("alarm list : {}", alarmList);

        for (InformAlarm alarm : alarmList) {
            InformIsRead informIsRead = informIsReadRepository.findByFcmTokenAndInformAlarm(requestDto.fcmToken(),
                    alarm.getId());
            InformAlarmListResponseDto informAlarmListResponseDto = InformAlarmListResponseDto.fromEntity(alarm,
                    informIsRead.getIsRead());
            informAlarmListResponseDtoList.add(informAlarmListResponseDto);
        }
        return informAlarmListResponseDtoList;
    }


    // 공지사항 알림 (2 depth)
    public InformAlarmResponseDto readInformDetail(InformDetailDto requestDto) {

        String fcmToken = requestDto.fcmToken();
        Long informId = Long.valueOf(requestDto.informId());

        log.info("dto : {}", requestDto);
        log.info("fcmToken : {}", fcmToken);
        log.info("inform ID : {}", informId);

        // isRead
        InformIsRead informIsRead = informIsReadRepository.findByFcmTokenAndInformAlarm(fcmToken, informId);
        log.info("inform : {}", informIsRead);

        informIsRead.markAsRead();
        informIsReadRepository.save(informIsRead);

        // informAlarm
        InformAlarm informAlarm = informAlarmRepository.findById(informId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_INFO_ALARM));

        // informAlarm img
        Optional<InformAlarmImage> img = informAlarmImageRepository.findByAlarmId(informId);
        if (img.isEmpty()) {
            throw new CommonException(ErrorCode.NOT_FOUND_INFO_IMG);
        }

        // InformAlarmResponseDto 객체 만들기
        else {
            InformAlarmResponseDto informAlarmResponseDto = InformAlarmResponseDto.builder()
                    .id(String.valueOf(informAlarm.getId()))
                    .title(informAlarm.getTitle())
                    .body(informAlarm.getBody())
                    .posterUrl(img.get().getPosterUrl())
                    .createdAt(informAlarm.getCreatedAt())
                    .build();
            return informAlarmResponseDto;
        }
    }

}
