package com.poppin.poppinserver.core.type;

public enum EVisitStatus {
    VN("VISIT_NOW"),             // 방문하기
    VC("VISIT_COMPLETE"),        // 방문 완료
    RRO("RECEIVE_REOPEN_ALERT"), // ??
    AC("ALERT_COMPLETE");        // 재오픈 신청 완료 상태

    private final String value;

    EVisitStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EVisitStatus fromValue(String value) {
        for (EVisitStatus EVisitStatus : EVisitStatus.values()) {
            if (EVisitStatus.getValue().equalsIgnoreCase(value)) {
                return EVisitStatus;
            }
        }
        throw new IllegalArgumentException("No matching enum constant for EVisitStatus value: " + value);
    }
}
