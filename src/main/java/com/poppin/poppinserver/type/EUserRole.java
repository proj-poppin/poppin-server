package com.poppin.poppinserver.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EUserRole {
    USER("USER", "ROLE_USER"),
    ADMIN("ADMIN", "ROLE_ADMIN"),
    GUEST("GUEST", "ROLE_GUEST");

    private final String name;
    private final String securityName;
}
