package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.alarm.request.AlarmTokenRequestDto;
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
    @PostMapping("/read")
    public ResponseDto<?> readAlarm(@UserId Long userId, @RequestBody AlarmTokenRequestDto alarmTokenRequestDto){
        return ResponseDto.ok(alarmService.readAlarm(userId, alarmTokenRequestDto));
    }

    // 팝업 알림 보여주기(1 depth)
    @PostMapping("/popup")
    public ResponseDto<?> readPopupAlarm(@UserId Long userId, @RequestBody AlarmTokenRequestDto alarmTokenRequestDto){
        return ResponseDto.ok(alarmService.readPopupAlarmList(userId, alarmTokenRequestDto));
    }

    // 팝업 알림 보여주기(2 depth)
    @GetMapping("/popup/detail")
    public ResponseDto<?> readPopupDetail(@UserId Long userId, @RequestParam("popupId") Long popupId){
        return ResponseDto.ok(alarmService.readPopupDetail(userId, popupId));
    }


    // 공지사항 알림 보여주기(1 depth)
    @GetMapping("/info")
    public ResponseDto<?> readInfoAlarm(){
        return ResponseDto.ok(alarmService.readInformAlarmList());
    }

    // 공지사항 디테일 (2 depth)
    @GetMapping("/info/detail")
    public ResponseDto<?> readDetailInfoAlarm(@UserId Long userId, @RequestParam("informId")Long informId){
        return ResponseDto.ok(alarmService.readDetailInformAlarm(userId, informId));
    }


    @PostMapping("/update/setting")
    public ResponseDto<?> createAlarmSetting(@UserId Long userId, @RequestBody AlarmSettingRequestDto dto){
        return ResponseDto.ok(alarmSettingService.updateAlarmSetting(userId, dto));
    }

    @PostMapping("/read/setting")
    public ResponseDto<?> readAlarmSetting(@UserId Long userId, @RequestBody AlarmTokenRequestDto dto){
        return ResponseDto.ok(alarmSettingService.readAlarmSetting(userId,dto));
    }


}
