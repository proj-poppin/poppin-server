package com.poppin.poppinserver.dto.user.response;

import lombok.Builder;

import java.util.List;

@Builder
public record UserListDto(
        List<UserAdministrationDto> userList,
        int userCnt
) {
}
