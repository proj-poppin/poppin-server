package com.poppin.poppinserver.admin.dto.response;

import lombok.Builder;

@Builder
public record AdminFaqResponseDto(
        Long faqId,
        String question,
        String answer,
        String createdAt
) {
}
