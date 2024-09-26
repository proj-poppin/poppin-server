package com.poppin.poppinserver.core.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ECongestionRate {
    LOW("여유"),
    MEDIUM("보통"),
    HIGH("혼잡");

    private final String rate;
}
