package com.poppin.poppinserver.core.type;

import com.poppin.poppinserver.core.exception.ErrorCode;

public enum ESatisfaction {

    SATISFIED("만족"),
    NORMAL("보통"),
    UNSATISFIED("불만족"),
    ;

    private final String value;
    ESatisfaction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ESatisfaction fromValue(String value) {
        for (ESatisfaction ESatisfaction : ESatisfaction.values()) {
            if (ESatisfaction.getValue().equalsIgnoreCase(value)) {
                return ESatisfaction;
            }
        }
        throw new IllegalArgumentException(String.valueOf(ErrorCode.SERVER_ERROR));
    }
}
