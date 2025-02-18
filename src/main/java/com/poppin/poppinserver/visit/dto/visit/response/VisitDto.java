package com.poppin.poppinserver.visit.dto.visit.response;

import lombok.Builder;

@Builder
public record VisitDto(
        String id,
        String popupId,
        String userId,
        String createdAt
) {
    public static VisitDto fromEntity(
            Long id,
            Long popupId,
            Long userId,
            String createdAt

    ){
        return VisitDto.builder()
                .id(String.valueOf(id))
                .popupId(String.valueOf(popupId))
                .userId(String.valueOf(userId))
                .createdAt(createdAt)
                .build();
    }
}
