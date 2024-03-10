package com.poppin.poppinserver.security.info;

import com.poppin.poppinserver.type.EUserRole;
import lombok.Builder;

@Builder
public record JwtUserInfo(Long id, EUserRole role) {
}
