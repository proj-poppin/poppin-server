package com.poppin.poppinserver.dto.alarm.response;

import com.poppin.poppinserver.domain.InformAlarmImage;
import com.poppin.poppinserver.domain.InformAlarm;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record InformApplyResponseDto(

        String title,
        String body,
        Long informId, // 공지사항 id
        LocalDate createdAt,
        String iconUrl,
        List<InformAlarmImage> informAlarmImages
) {
    public static InformApplyResponseDto fromEntity(InformAlarm alarm, List<InformAlarmImage> informAlarmImages)
    {
        return InformApplyResponseDto.builder()
                .title(alarm.getTitle())
                .body(alarm.getBody())
                .informId(alarm.getId()) // 공지사항 id
                .createdAt(alarm.getCreatedAt())
                .iconUrl(alarm.getIcon())
                .informAlarmImages(informAlarmImages)
                .build();
    }
}
