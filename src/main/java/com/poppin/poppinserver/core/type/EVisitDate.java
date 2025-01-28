package com.poppin.poppinserver.core.type;

public enum EVisitDate {
    WEEKDAY_AM("평일 오전"),
    WEEKDAY_PM("평일 오후"),
    WEEKEND_AM("주말 오전"),
    WEEKEND_PM("주말 오후");

    private final String value;

    EVisitDate(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EVisitDate fromValue(String value) {
        for (EVisitDate EVisitDate : EVisitDate.values()) {
            if (EVisitDate.getValue().equalsIgnoreCase(value)) {
                return EVisitDate;
            }
        }
        throw new IllegalArgumentException("No matching enum constant for value: " + value);
    }
}
