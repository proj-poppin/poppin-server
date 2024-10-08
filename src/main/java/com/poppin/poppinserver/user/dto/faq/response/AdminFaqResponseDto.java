package com.poppin.poppinserver.user.dto.faq.response;

import lombok.Builder;

@Builder
public record AdminFaqResponseDto(
        Long faqId,
        String question,
        String answer,
        String createdAt
) {
}
