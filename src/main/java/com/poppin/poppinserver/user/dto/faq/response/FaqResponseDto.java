package com.poppin.poppinserver.user.dto.faq.response;

import lombok.Builder;

@Builder
public record FaqResponseDto(
        Long id,
        String question,
        String answer,
        String createdAt
) {
}
