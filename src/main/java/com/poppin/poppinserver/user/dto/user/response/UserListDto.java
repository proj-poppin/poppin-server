package com.poppin.poppinserver.user.dto.user.response;

import lombok.Builder;

import java.util.List;

@Builder
public record UserListDto(
        List<UserAdministrationDto> userList,
        Long userCnt
) {
}
