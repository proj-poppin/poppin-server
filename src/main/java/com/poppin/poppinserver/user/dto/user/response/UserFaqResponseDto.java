package com.poppin.poppinserver.user.dto.user.response;

import com.poppin.poppinserver.admin.domain.FreqQuestion;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record UserFaqResponseDto(
        Long faqId,
        String question,
        String answer,
        String createdAt
) {
    public static UserFaqResponseDto fromFaqEntity(FreqQuestion freqQuestion) {
        return UserFaqResponseDto.builder()
                .faqId(freqQuestion.getId())
                .question(freqQuestion.getQuestion())
                .answer(freqQuestion.getAnswer())
                .createdAt(freqQuestion.getCreatedAt().toString())
                .build();
    }
}
