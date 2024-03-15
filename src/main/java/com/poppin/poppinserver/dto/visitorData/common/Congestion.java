package com.poppin.poppinserver.dto.visitorData.common;

import com.poppin.poppinserver.exception.ErrorCode;

public enum Congestion {

    CROWDED("혼잡"),
    NORMAL("보통"),
    RELAXED("여유"),
    ;

    private final String value;
    Congestion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Congestion fromValue(String value) {
        for (Congestion congestion : Congestion.values()) {
            if (congestion.getValue().equalsIgnoreCase(value)) {
                return congestion;
            }
        }
        throw new IllegalArgumentException(String.valueOf(ErrorCode.SERVER_ERROR));
    }
}
