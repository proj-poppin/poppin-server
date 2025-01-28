package com.poppin.poppinserver.core.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EPopupSort {
    RECENTLY_OPENED("RECENTLY_OPENED"),
    CLOSING_SOON("CLOSING_SOON"),
    MOST_VIEWED("MOST_VIEWED"),
    RECENTLY_UPLOADED("RECENTLY_UPLOADED");

    private final String sort;
}
