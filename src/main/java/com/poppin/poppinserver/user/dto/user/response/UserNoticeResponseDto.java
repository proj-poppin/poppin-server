package com.poppin.poppinserver.user.dto.user.response;

import lombok.AccessLevel;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
public record UserNoticeResponseDto(
        String lastCheck,   // 마지막으로 공지를 확인한 시간
        List<String> checkedNoticeIds   // 확인한 공지 ID 리스트
) {
    public static UserNoticeResponseDto of(LocalDateTime lastCheck, List<String> checkedNoticeIds) {
        String formattedTime = lastCheck != null ? lastCheck.toString() : null;

        return UserNoticeResponseDto.builder()
                .lastCheck(formattedTime)
                .checkedNoticeIds(checkedNoticeIds)
                .build();
    }
}
