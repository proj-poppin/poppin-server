package com.poppin.poppinserver.user.dto.admin;

import com.poppin.poppinserver.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record AdminInfoResponseDto(
        String userImageUrl, // 유저 이미지 URL
        String email, // 이메일
        String nickname, // 닉네임
        String provider // 제공자
) {
    public static AdminInfoResponseDto fromEntity(User user) {
        return AdminInfoResponseDto.builder()
                .userImageUrl(user.getProfileImageUrl())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .provider(user.getProvider().name())
                .build();
    }
}
