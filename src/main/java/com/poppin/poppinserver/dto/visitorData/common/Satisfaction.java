package com.poppin.poppinserver.dto.visitorData.common;

import com.poppin.poppinserver.exception.ErrorCode;

public enum Satisfaction {

    SATISFIED("만족"),
    NORMAL("보통"),
    UNSATISFIED("불만족"),
    ;

    private final String value;
    Satisfaction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Satisfaction fromValue(String value) {
        for (Satisfaction satisfaction : Satisfaction.values()) {
            if (satisfaction.getValue().equalsIgnoreCase(value)) {
                return satisfaction;
            }
        }
        throw new IllegalArgumentException(String.valueOf(ErrorCode.SERVER_ERROR));
    }
}
