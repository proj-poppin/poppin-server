package com.poppin.poppinserver.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EOperationStatus {
    NOTYET("NOTYET"),
    OPERATING("OPERATING"),
    TERMINATED("TERMINATED"),
    EXECUTING("EXECUTING");

    private final String status;
}
