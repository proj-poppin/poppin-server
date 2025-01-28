package com.poppin.poppinserver.user.dto.auth.request;

import lombok.Builder;

@Builder
public record AppStartRequestDto(
        String os,
        String appVersion
) {
}
