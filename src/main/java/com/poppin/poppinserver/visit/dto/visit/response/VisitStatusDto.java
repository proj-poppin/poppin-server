package com.poppin.poppinserver.visit.dto.visit.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record VisitStatusDto(
        String id,
        String popupId,
        String userId,
        String status,
        String createdAt
) {
    public static VisitStatusDto fromEntity(
            Long id,
            Long popupId,
            Long userId,
            String status,
            LocalDate createdAt

    ){
        return VisitStatusDto.builder()
                .id(String.valueOf(id))
                .popupId(String.valueOf(popupId))
                .userId(String.valueOf(userId))
                .status(status)
                .createdAt(createdAt.toString())
                .build();
    }
}
