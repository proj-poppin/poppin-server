package com.poppin.poppinserver.user.dto.user.response;

import lombok.Builder;

@Builder
public record UserFaqResponseDto(
        String faqId,
        String question,
        String answer,
        String createdAt
) {
}
