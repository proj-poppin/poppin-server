package com.poppin.poppinserver.admin.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record UserAdministrationListResponseDto(
        List<UserAdministrationResponseDto> userList,
        Long userCnt
) {
}
