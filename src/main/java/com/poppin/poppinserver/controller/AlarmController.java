package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.alarm.request.FcmTokenAlarmRequestDto;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;




@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/alarm")
public class AlarmController {

    private final AlarmService alarmService;

    // 팝업 알림 보여주기
    @PostMapping("/popup")
    public ResponseDto<?> readPopupAlarm(@UserId Long userId, @RequestBody FcmTokenAlarmRequestDto fcmTokenAlarmRequestDto){
        return ResponseDto.ok(alarmService.readPopupAlarmList(userId, fcmTokenAlarmRequestDto));
    }

    // 공지사항 알림 보여주기
    @GetMapping("/info")
    public ResponseDto<?> readInfoAlarm(){
        return ResponseDto.ok(alarmService.readInformAlarmList());
    }

    // 공지사항 디테일 (1 depth more)
    //public ResponseDto<?> read
}
