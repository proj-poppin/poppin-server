package com.poppin.poppinserver.user.dto.user.response;

import com.poppin.poppinserver.user.domain.User;
import lombok.Builder;

// TODO: 삭제 예정
@Builder
public record UserDto(
        Long id,
        String email,
        String nickname,
        String provider,
        String role,
        Boolean agreedToPrivacyPolicy,
        Boolean agreedToServiceTerms,
        String createdAt
) {
    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .provider(user.getProvider().toString())
                .role(user.getRole().toString())
                .agreedToPrivacyPolicy(user.getAgreedToPrivacyPolicy())
                .agreedToServiceTerms(user.getAgreedToServiceTerms())
                .createdAt(user.getCreatedAt().toString())
                .build();
    }
}