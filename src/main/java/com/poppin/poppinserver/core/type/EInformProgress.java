package com.poppin.poppinserver.core.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EInformProgress {
    NOTEXECUTED("NOTEXECUTED"),
    EXECUTING("EXECUTING"),
    EXECUTED("EXECUTED");

    private final String progressProvider;
}
