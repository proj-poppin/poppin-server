package com.poppin.poppinserver.alarm.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ENotificationCategory {
    POPUP("POPUP"),
    NOTICE("NOTICE");

    private final String category;
}
