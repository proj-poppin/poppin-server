package com.poppin.poppinserver.alarm.controller.swagger;

import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmKeywordRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmTokenRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.request.InformDetailDto;
import com.poppin.poppinserver.alarm.dto.alarmSetting.request.AlarmSettingRequestDto;
import com.poppin.poppinserver.core.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "알람", description = "알림 관련 API")
public interface SwaggerAlarmController {

    @Operation(summary = "알림 읽음 여부 조회", description = "사용자의 알림 읽음 여부를 확인합니다.")
    @PostMapping("/unread")
    ResponseDto<?> readAlarm(@RequestBody AlarmTokenRequestDto alarmTokenRequestDto);

    @Operation(summary = "팝업 알림 목록 조회", description = "팝업 알림 목록을 조회합니다.")
    @PostMapping("/popup")
    ResponseDto<?> readPopupAlarm(@RequestBody AlarmTokenRequestDto alarmTokenRequestDto);

    @Operation(summary = "공지사항 알림 목록 조회", description = "공지사항 알림 목록을 조회합니다.")
    @PostMapping("/info")
    ResponseDto<?> readInfoAlarm(@RequestBody AlarmTokenRequestDto requestDto);

    @Operation(summary = "공지사항 알림 상세 조회", description = "공지사항 알림의 상세 내용을 조회합니다.")
    @PostMapping("/info/detail")
    ResponseDto<?> readDetailInfoAlarm(@RequestBody InformDetailDto requestDto);

    @Operation(summary = "알림 설정 조회", description = "사용자의 알림 설정을 조회합니다.")
    @PostMapping("/read/setting")
    ResponseDto<?> readAlarmSetting(
            @Parameter(hidden = true) Long userId,
            @RequestBody AlarmTokenRequestDto dto
    );

    @Operation(summary = "알림 설정 수정", description = "사용자의 알림 설정을 수정합니다.")
    @PutMapping("/settings")
    ResponseDto<?> createAlarmSetting(
            @Parameter(hidden = true) Long userId,
            @RequestBody AlarmSettingRequestDto dto
    );

    @Operation(summary = "알림 키워드 조회", description = "사용자의 알림 키워드를 조회합니다.")
    @GetMapping("/keywords")
    ResponseDto<?> readAlarmKeywords(
            @Parameter(hidden = true) Long userId
    );

    @Operation(summary = "알림 키워드 등록", description = "새로운 알림 키워드를 등록합니다.")
    @PostMapping("/keywords")
    ResponseDto<?> createAlarmKeyword(
            @Parameter(hidden = true) Long userId,
            @RequestBody AlarmKeywordRequestDto alarmKeywordRequestDto
    );

    @Operation(summary = "알림 키워드 삭제", description = "알림 키워드를 삭제합니다.")
    @DeleteMapping("/keywords/{keywordId}")
    ResponseDto<?> deleteAlarmKeyword(
            @Parameter(hidden = true) Long userId,
            @PathVariable(name = "keywordId") Long keywordId
    );

    @Operation(summary = "알림 키워드 활성화/비활성화", description = "알림 키워드의 활성화 상태를 변경합니다.")
    @PutMapping("/keywords/{keywordId}")
    ResponseDto<?> setAlarmKeywordStatus(
            @Parameter(hidden = true) Long userId,
            @PathVariable(name = "keywordId") Long keywordId,
            @RequestParam(name = "isOn") Boolean isOn
    );
}
