package com.poppin.poppinserver.core.type;

import com.poppin.poppinserver.core.exception.ErrorCode;

public enum ECongestion {

    CROWDED("혼잡"),
    NORMAL("보통"),
    RELAXED("여유"),
    ;

    private final String value;
    ECongestion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ECongestion fromValue(String value) {
        for (ECongestion ECongestion : ECongestion.values()) {
            if (ECongestion.getValue().equalsIgnoreCase(value)) {
                return ECongestion;
            }
        }
        throw new IllegalArgumentException(String.valueOf(ErrorCode.SERVER_ERROR));
    }
}
