//package com.poppin.poppinserver.legacy.alarm.controller;
//
//import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmPopupRequestDto;
//import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmTokenRequestDto;
//import com.poppin.poppinserver.alarm.service.AlarmKeywordService;
//import com.poppin.poppinserver.alarm.service.AlarmListService;
//import com.poppin.poppinserver.alarm.service.AlarmService;
//import com.poppin.poppinserver.core.annotation.UserId;
//import com.poppin.poppinserver.core.dto.ResponseDto;
//import com.poppin.poppinserver.legacy.alarm.service.AlarmSettingService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@Slf4j
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/alarm")
//public class AlarmController {
//    private final AlarmService alarmService;
//    private final AlarmListService alarmListService;
//    private final AlarmSettingService alarmSettingService;
//    private final AlarmKeywordService alarmKeywordService;
//
//    //TODO: 삭제 예정
//    // 팝업 알림 보여주기 - 로그인 (2 depth)
//    @PostMapping("/detail/popup")
//    public ResponseDto<?> readPopupDetail(@UserId Long userId, @RequestBody AlarmPopupRequestDto requestDto) {
//        return ResponseDto.ok(alarmListService.readPopupDetail(userId, requestDto));
//    }
//    //TODO: 삭제 예정
//    // 팝업 알림 보여주기 - 비로그인 (2 depth)
//    @PostMapping("/popup/guest/detail")
//    public ResponseDto<?> readPopupDetailGuest(@RequestBody AlarmPopupRequestDto requestDto) {
//        return ResponseDto.ok(alarmListService.readPopupDetailGuest(requestDto));
//    }
//
//    // TODO: 삭제 예정
//    @PostMapping("/read/setting")
//    public ResponseDto<?> readAlarmSetting(@UserId Long userId, @RequestBody AlarmTokenRequestDto dto) {
//        return ResponseDto.ok(alarmSettingService.readAlarmSetting(userId, dto));
//    }
//}
