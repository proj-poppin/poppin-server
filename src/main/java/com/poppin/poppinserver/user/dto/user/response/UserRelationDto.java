package com.poppin.poppinserver.user.dto.user.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record UserRelationDto(
        List<String> blockedUserIds,
        List<String> blockedPopupIds
) {
    static public UserRelationDto ofBlockedUserIdsAndPopupIds(List<String> blockedUserIds,
                                                              List<String> blockedPopupIds) {
        return UserRelationDto.builder()
                .blockedUserIds(blockedUserIds)
                .blockedPopupIds(blockedPopupIds)
                .build();
    }
}

