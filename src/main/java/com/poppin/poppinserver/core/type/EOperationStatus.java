package com.poppin.poppinserver.core.type;

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
