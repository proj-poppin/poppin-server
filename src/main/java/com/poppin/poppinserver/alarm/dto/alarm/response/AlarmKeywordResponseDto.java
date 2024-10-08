package com.poppin.poppinserver.alarm.dto.alarm.response;

import com.poppin.poppinserver.alarm.domain.UserAlarmKeyword;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Builder
public record AlarmKeywordResponseDto(
        String keywordId,
        String keyword,
        boolean isOn
) {
    public static List<AlarmKeywordResponseDto> fromEntity(Set<UserAlarmKeyword> userAlarmKeywords) {
        List<AlarmKeywordResponseDto> AlarmKeywordDtoList = new ArrayList<>();

        for (UserAlarmKeyword userAlarmKeyword : userAlarmKeywords) {
            AlarmKeywordResponseDto alarmKeywordResponseDto = AlarmKeywordResponseDto.builder()
                    .keywordId(String.valueOf(userAlarmKeyword.getId()))
                    .keyword(userAlarmKeyword.getKeyword())
                    .isOn(userAlarmKeyword.getIsOn())
                    .build();

            AlarmKeywordDtoList.add(alarmKeywordResponseDto);
        }

        return AlarmKeywordDtoList;
    }
}
