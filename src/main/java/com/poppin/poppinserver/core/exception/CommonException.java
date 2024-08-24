package com.poppin.poppinserver.core.exception;

public class CommonException extends RuntimeException {
    private final ErrorCode errorCode;

    public CommonException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
