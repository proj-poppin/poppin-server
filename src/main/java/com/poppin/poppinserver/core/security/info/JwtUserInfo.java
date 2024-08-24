package com.poppin.poppinserver.core.security.info;

import com.poppin.poppinserver.core.type.EUserRole;
import lombok.Builder;

@Builder
public record JwtUserInfo(Long id, EUserRole role) {
}
