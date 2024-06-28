package com.poppin.poppinserver.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EOperationStatus {
    NOTYET("NOTYET"),
    OPERATING("OPERATING"),
    TERMINATED("TERMINATED"),
    EXECUTING("EXECUTING");

    private final String status;
}
