package com.poppin.poppinserver.security.info;

import com.poppin.poppinserver.type.EUserRole;
import lombok.Builder;

@Builder
public record JwtUserInfo(String email, EUserRole role) {
}
