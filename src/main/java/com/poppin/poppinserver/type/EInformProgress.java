package com.poppin.poppinserver.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EInformProgress {
    NOTEXECUTED("NOTEXECUTED"),
    EXECUTING("EXECUTING"),
    EXECUTED("EXECUTED");

    private final String progressProvider;
}
