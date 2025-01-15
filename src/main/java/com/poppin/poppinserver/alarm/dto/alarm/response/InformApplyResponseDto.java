package com.poppin.poppinserver.alarm.dto.alarm.response;

import com.poppin.poppinserver.alarm.domain.InformAlarm;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record InformApplyResponseDto(

        String title,
        String body,
        String informId, // 공지사항 id
        LocalDate createdAt,
        String iconUrl,
        List<String> posterUrl
) {
    public static InformApplyResponseDto fromEntity(InformAlarm alarm, List<String> posterUrl) {
        return InformApplyResponseDto.builder()
                .title(alarm.getTitle())
                .body(alarm.getBody())
                .informId(String.valueOf(alarm.getId())) // 공지사항 id
                .createdAt(alarm.getCreatedAt().toLocalDate())
                .iconUrl(alarm.getIcon())
                .posterUrl(posterUrl)
                .build();
    }
}
