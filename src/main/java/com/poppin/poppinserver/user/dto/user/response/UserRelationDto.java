package com.poppin.poppinserver.user.dto.user.response;

import lombok.Builder;

import java.util.List;

@Builder
public record UserRelationDto(
        List<String> blockedUserIds
) {
    static public UserRelationDto ofBlockedUserIds(List<String> blockedUserIds) {
        return UserRelationDto.builder()
                .blockedUserIds(blockedUserIds)
                .build();
    }
}
