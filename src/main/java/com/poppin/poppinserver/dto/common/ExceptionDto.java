package com.poppin.poppinserver.dto.common;

import com.poppin.poppinserver.exception.ErrorCode;
import lombok.Getter;

// 정의해 놓은 ErrorCode를 받으면 에러 코드랑 메세지만 추출
@Getter
public class ExceptionDto {
    private final String code;
    private final String message;

    public ExceptionDto(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}