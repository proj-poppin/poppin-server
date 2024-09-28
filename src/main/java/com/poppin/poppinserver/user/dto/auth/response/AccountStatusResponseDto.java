package com.poppin.poppinserver.user.dto.auth.response;

import com.poppin.poppinserver.user.domain.type.EAccountStatus;
import lombok.Builder;

@Builder
public record AccountStatusResponseDto(
        String accountStatus
) {
    public static AccountStatusResponseDto fromEnum(EAccountStatus accountStatus) {
        return AccountStatusResponseDto.builder()
                .accountStatus(accountStatus.toString())
                .build();
    }
}
