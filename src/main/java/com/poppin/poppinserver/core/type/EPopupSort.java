package com.poppin.poppinserver.core.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EPopupSort {
    OPEN("OPEN"),
    CLOSE("CLOSE"),
    VIEW("VIEW"),
    UPLOAD("UPLOAD");

    private final String sort;
}
