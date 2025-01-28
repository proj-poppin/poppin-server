package com.poppin.poppinserver.alarm.controller;

import com.poppin.poppinserver.alarm.controller.swagger.SwaggerAlarmController;
import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmKeywordRequestDto;
import com.poppin.poppinserver.alarm.service.AlarmKeywordService;
import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/alarm")
public class AlarmController implements SwaggerAlarmController {
    private final AlarmKeywordService alarmKeywordService;

    // TODO : 삭제 예정
    // 알림 안읽은 것 여부
//    @PostMapping("/unread")
//    public ResponseDto<?> readAlarm(@RequestBody AlarmTokenRequestDto alarmTokenRequestDto) {
//        return ResponseDto.ok(alarmService.readAlarm(alarmTokenRequestDto));
//    }

    // 팝업 알림 보여주기(1 depth)
//    @PostMapping("/popup")
//    public ResponseDto<?> readPopupAlarm(@RequestBody AlarmTokenRequestDto alarmTokenRequestDto) {
//        return ResponseDto.ok(alarmListService.readPopupAlarmList(alarmTokenRequestDto));
//    }
//
//    // 공지사항 알림 보여주기(1 depth)
//    @PostMapping("/info")
//    public ResponseDto<?> readInfoAlarm(@RequestBody AlarmTokenRequestDto requestDto) {
//        return ResponseDto.ok(alarmListService.readInformAlarmList(requestDto));
//    }
//
//    // 공지사항 디테일 (2 depth)
//    @PostMapping("/info/detail")
//    public ResponseDto<?> readDetailInfoAlarm(@RequestBody InformDetailDto requestDto) {
//        return ResponseDto.ok(alarmListService.readInformDetail(requestDto));
//    }

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
        return ResponseDto.ok(null);
    }

    // 마이페이지 > 키워드 알람 > 키워드 활성화/비활성화
    @PutMapping("/keywords/{keywordId}")
    public ResponseDto<?> setAlarmKeywordStatus(@UserId Long userId,
                                                @PathVariable(name = "keywordId") Long keywordId,
                                                @RequestParam(name = "isOn") Boolean isOn) {
        return ResponseDto.ok(alarmKeywordService.setAlarmKeywordStatus(userId, keywordId, isOn));
    }
}
