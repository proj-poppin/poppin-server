package com.poppin.poppinserver.user.dto.user.response;

import lombok.Builder;

import java.util.List;

@Builder
public record UserRelationDto(
        List<String> blockedUserIds,
        List<String> blockedPopupIds
) {
    static public UserRelationDto ofBlockedUserIdsAndPopupIds(List<String> blockedUserIds, List<String> blockedPopupIds) {
        return UserRelationDto.builder()
                .blockedUserIds(blockedUserIds)
                .blockedPopupIds(blockedPopupIds)
                .build();
    }
}
