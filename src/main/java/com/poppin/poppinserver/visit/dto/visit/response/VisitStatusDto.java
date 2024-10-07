package com.poppin.poppinserver.visit.dto.visit.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record VisitStatusDto(
        Long id,
        Long popupId,
        Long userId,
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
                .id(id)
                .popupId(popupId)
                .userId(userId)
                .status(status)
                .createdAt(createdAt.toString())
                .build();
    }
}
