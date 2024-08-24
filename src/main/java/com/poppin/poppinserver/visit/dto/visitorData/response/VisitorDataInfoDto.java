package com.poppin.poppinserver.visit.dto.visitorData.response;

import lombok.Builder;

import java.util.Map;
import java.util.Optional;

@Builder
public record VisitorDataInfoDto(
        Map<String,Object> weekdayAm,
        Map<String,Object> weekdayPm,
        Map<String,Object> weekendAm,
        Map<String,Object> weekendPm,
        Optional<Integer> satisfaction
) {
    public static VisitorDataInfoDto fromEntity(
            Map<String,Object> weekdayAm,
            Map<String,Object> weekdayPm,
            Map<String,Object> weekendAm,
            Map<String,Object> weekendPm,
            Optional<Integer> satisfaction
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
