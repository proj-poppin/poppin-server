package com.poppin.poppinserver.dto.visitorData.common;

public enum VisitDate {
    WEEKDAY_AM("평일 오전"),
    WEEKDAY_PM("평일 오후"),
    WEEKEND_AM("주말 오전"),
    WEEKEND_PM("주말 오후");

    private final String value;
    VisitDate(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static VisitDate fromValue(String value) {
        for (VisitDate visitDate : VisitDate.values()) {
            if (visitDate.getValue().equalsIgnoreCase(value)) {
                return visitDate;
            }
        }
        throw new IllegalArgumentException("No matching enum constant for value: " + value);
    }
}
