package com.poppin.poppinserver.dto.visitorData.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import java.util.Map;

@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record VisitorDataInfoDto(
        Map<String,Object> weekdayAm,
        Map<String,Object> weekdayPm,
        Map<String,Object> weekendAm,
        Map<String,Object> weekendPm,
        Integer satisfaction
) {
    public static VisitorDataInfoDto fromEntity(
            Map<String,Object> weekdayAm,
            Map<String,Object> weekdayPm,
            Map<String,Object> weekendAm,
            Map<String,Object> weekendPm,
            Integer satisfaction
    ){
        return VisitorDataInfoDto.builder()
                .weekdayAm(weekdayAm)
                .weekdayPm(weekdayPm)
                .weekendAm(weekendAm)
                .weekendPm(weekendPm)
                .satisfaction(satisfaction)
                .build();
    }
}
