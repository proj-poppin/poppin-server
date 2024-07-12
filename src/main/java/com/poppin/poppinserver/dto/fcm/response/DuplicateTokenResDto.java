package com.poppin.poppinserver.dto.fcm.response;

import lombok.Builder;

@Builder
public record DuplicateTokenResDto(
        Boolean isDuplicated
) {
    public static DuplicateTokenResDto fromEntity(Boolean status){
        return DuplicateTokenResDto.builder().isDuplicated(status).build();
    }
}
