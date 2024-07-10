package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.alarm.request.AlarmPopupRequestDto;
import com.poppin.poppinserver.dto.alarm.request.AlarmTokenRequestDto;
import com.poppin.poppinserver.dto.alarm.request.InformDetailDto;
import com.poppin.poppinserver.dto.alarmSetting.request.AlarmSettingRequestDto;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.service.AlarmService;
import com.poppin.poppinserver.service.AlarmSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;




@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/alarm")
public class AlarmController {

    private final AlarmService alarmService;
    private final AlarmSettingService alarmSettingService;

    // 알림 안읽은 것 여부
    @PostMapping("/unread")
    public ResponseDto<?> readAlarm( @RequestBody AlarmTokenRequestDto alarmTokenRequestDto){
        return ResponseDto.ok(alarmService.readAlarm(alarmTokenRequestDto));
    }

    // 팝업 알림 보여주기(1 depth)
    @PostMapping("/popup")
    public ResponseDto<?> readPopupAlarm(@RequestBody AlarmTokenRequestDto alarmTokenRequestDto){
        return ResponseDto.ok(alarmService.readPopupAlarmList(alarmTokenRequestDto));
    }

    // 팝업 알림 보여주기 - 로그인 (2 depth)
    @PostMapping("/detail/popup")
    public ResponseDto<?> readPopupDetail(@UserId Long userId, @RequestBody AlarmPopupRequestDto requestDto){
        return ResponseDto.ok(alarmService.readPopupDetail(userId, requestDto));
    }

    // 팝업 알림 보여주기 - 비로그인 (2 depth)
    @PostMapping("/popup/guest/detail")
    public ResponseDto<?> readPopupDetailGuest(@RequestBody AlarmPopupRequestDto requestDto){
        return ResponseDto.ok(alarmService.readPopupDetailGuest(requestDto));
    }

    // 공지사항 알림 보여주기(1 depth)
    @PostMapping("/info")
    public ResponseDto<?> readInfoAlarm(@RequestBody AlarmTokenRequestDto requestDto){
        return ResponseDto.ok(alarmService.readInformAlarmList(requestDto));
    }

    // 공지사항 디테일 (2 depth)
    @PostMapping("/info/detail")
    public ResponseDto<?> readDetailInfoAlarm(@RequestBody InformDetailDto requestDto){
        return ResponseDto.ok(alarmService.readInformDetail(requestDto));
    }

    @PostMapping("/read/setting")
    public ResponseDto<?> readAlarmSetting(@UserId Long userId, @RequestBody AlarmTokenRequestDto dto){
        return ResponseDto.ok(alarmSettingService.readAlarmSetting(userId,dto));
    }

    @PostMapping("/update/setting")
    public ResponseDto<?> createAlarmSetting(@UserId Long userId, @RequestBody AlarmSettingRequestDto dto){
        return ResponseDto.ok(alarmSettingService.updateAlarmSetting(userId, dto));
    }

}
