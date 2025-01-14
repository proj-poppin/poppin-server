package com.poppin.poppinserver.user.dto.user.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record UserNoticeResponseDto(
        String lastCheck,   // 마지막으로 공지를 확인한 시간
        List<String> checkedNoticeIds   // 확인한 공지 ID 리스트
) {
    public static UserNoticeResponseDto of(String lastCheck, List<String> checkedNoticeIds) {
        return UserNoticeResponseDto.builder()
                .lastCheck(lastCheck)
                .checkedNoticeIds(checkedNoticeIds)
                .build();
    }
}
