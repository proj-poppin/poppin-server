package com.poppin.poppinserver.alarm.controller;

import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmKeywordRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmPopupRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmTokenRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.request.InformDetailDto;
import com.poppin.poppinserver.alarm.dto.alarmSetting.request.AlarmSettingRequestDto;
import com.poppin.poppinserver.alarm.service.AlarmKeywordService;
import com.poppin.poppinserver.alarm.service.AlarmListService;
import com.poppin.poppinserver.alarm.service.AlarmService;
import com.poppin.poppinserver.alarm.service.AlarmSettingService;
import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/alarm")
public class AlarmController {

    private final AlarmService alarmService;
    private final AlarmListService alarmListService;
    private final AlarmSettingService alarmSettingService;
    private final AlarmKeywordService alarmKeywordService;

    // 알림 안읽은 것 여부
    @PostMapping("/unread")
    public ResponseDto<?> readAlarm(@RequestBody AlarmTokenRequestDto alarmTokenRequestDto) {
        return ResponseDto.ok(alarmService.readAlarm(alarmTokenRequestDto));
    }

    // 팝업 알림 보여주기(1 depth)
    @PostMapping("/popup")
    public ResponseDto<?> readPopupAlarm(@RequestBody AlarmTokenRequestDto alarmTokenRequestDto) {
        return ResponseDto.ok(alarmListService.readPopupAlarmList(alarmTokenRequestDto));
    }

    // 팝업 알림 보여주기 - 로그인 (2 depth)
    @PostMapping("/detail/popup")
    public ResponseDto<?> readPopupDetail(@UserId Long userId, @RequestBody AlarmPopupRequestDto requestDto) {
        return ResponseDto.ok(alarmListService.readPopupDetail(userId, requestDto));
    }

    // 팝업 알림 보여주기 - 비로그인 (2 depth)
    @PostMapping("/popup/guest/detail")
    public ResponseDto<?> readPopupDetailGuest(@RequestBody AlarmPopupRequestDto requestDto) {
        return ResponseDto.ok(alarmListService.readPopupDetailGuest(requestDto));
    }

    // 공지사항 알림 보여주기(1 depth)
    @PostMapping("/info")
    public ResponseDto<?> readInfoAlarm(@RequestBody AlarmTokenRequestDto requestDto) {
        return ResponseDto.ok(alarmListService.readInformAlarmList(requestDto));
    }

    // 공지사항 디테일 (2 depth)
    @PostMapping("/info/detail")
    public ResponseDto<?> readDetailInfoAlarm(@RequestBody InformDetailDto requestDto) {
        return ResponseDto.ok(alarmListService.readInformDetail(requestDto));
    }

    @PostMapping("/read/setting")
    public ResponseDto<?> readAlarmSetting(@UserId Long userId, @RequestBody AlarmTokenRequestDto dto) {
        return ResponseDto.ok(alarmSettingService.readAlarmSetting(userId, dto));
    }

    @PostMapping("/update/setting")
    public ResponseDto<?> createAlarmSetting(@UserId Long userId, @RequestBody AlarmSettingRequestDto dto) {
        return ResponseDto.ok(alarmSettingService.updateAlarmSetting(userId, dto));
    }

    // 마이페이지 > 키워드 알람 > 키워드 조회
    @GetMapping("/keywords")
    public ResponseDto<?> readAlarmKeywords(@UserId Long userId) {
        return ResponseDto.ok(alarmKeywordService.readAlarmKeywords(userId));
    }

    // 마이페이지 > 키워드 알람 > 키워드 등록
    @PostMapping("/keywords")
    public ResponseDto<?> createAlarmKeyword(@UserId Long userId,
                                             @RequestBody AlarmKeywordRequestDto alarmKeywordRequestDto) {
        return ResponseDto.ok(alarmKeywordService.createAlarmKeyword(userId, alarmKeywordRequestDto));
    }

    // 마이페이지 > 키워드 알람 > 키워드 삭제
    @DeleteMapping("/keywords/{keywordId}")
    public ResponseDto<?> deleteAlarmKeyword(@UserId Long userId, @PathVariable(name = "keywordId") Long keywordId) {
        alarmKeywordService.deleteAlarmKeyword(userId, keywordId);
        return ResponseDto.ok("알람 키워드가 삭제되었습니다.");
    }

    // 마이페이지 > 키워드 알람 > 키워드 활성화/비활성화
    @PutMapping("/keywords/{keywordId}")
    public ResponseDto<?> setAlarmKeywordStatus(@UserId Long userId,
                                                @PathVariable(name = "keywordId") Long keywordId,
                                                @RequestParam(name = "isOn") Boolean isOn) {
        return ResponseDto.ok(alarmKeywordService.setAlarmKeywordStatus(userId, keywordId, isOn));
    }
}
