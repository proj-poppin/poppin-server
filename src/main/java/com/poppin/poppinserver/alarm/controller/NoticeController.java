package com.poppin.poppinserver.alarm.controller;

import com.poppin.poppinserver.alarm.dto.informAlarm.response.NoticeDto;
import com.poppin.poppinserver.alarm.service.AlarmService;
import com.poppin.poppinserver.core.annotation.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/notices")
public class NoticeController {

    private final AlarmService alarmService;

    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeDto> getNotice(@UserId Long userId, @PathVariable Long noticeId) {
        // noticeId를 사용한 로직 처리
        NoticeDto notice = alarmService.getNoticeById(userId, noticeId);
        return ResponseEntity.ok(notice);
    }
}
