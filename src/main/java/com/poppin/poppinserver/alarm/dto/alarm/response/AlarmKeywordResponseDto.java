package com.poppin.poppinserver.alarm.dto.alarm.response;

import com.poppin.poppinserver.alarm.domain.AlarmKeyword;
import lombok.Builder;

@Builder
public record AlarmKeywordResponseDto(
        Long keywordId,
        String keyword,
        boolean isOn
) {
    public static AlarmKeywordResponseDto fromEntity(AlarmKeyword alarmKeyword) {
        return AlarmKeywordResponseDto.builder()
                .keywordId(alarmKeyword.getId())
                .keyword(alarmKeyword.getKeyword())
                .isOn(alarmKeyword.getIsOn())
                .build();
    }
}
