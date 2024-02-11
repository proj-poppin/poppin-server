package com.poppin.poppinserver.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //Bad Request Error
    INVALID_PARAMETER("40000", HttpStatus.BAD_REQUEST, "유효하지 않는 파라미터입니다."),
    MISSING_REQUEST_PARAMETER("40001", HttpStatus.BAD_REQUEST, "필수 파라미터가 누락되었습니다."),
    INVALID_ROLE("40002", HttpStatus.NOT_FOUND, "유효하지 않은 권한입니다."),
    INVALID_PROVIDER("40003", HttpStatus.NOT_FOUND, "유효하지 않은 제공자입니다."),
    INVALID_HEADER("40004", HttpStatus.NOT_FOUND, "유효하지 않은 헤더값입니다."),
    DUPLICATED_SERIAL_ID("40005", HttpStatus.NOT_FOUND, "중복된 아이디입니다."),
    PASSWORD_NOT_MATCH("40006", HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    DUPLICATED_NICKNAME("40007", HttpStatus.BAD_REQUEST, "중복된 닉네임입니다."),
    MISSING_REQUEST_BODY("40008", HttpStatus.BAD_REQUEST, "요청 바디가 누락되었습니다."),

    //Unauthorized Error
    FAILURE_LOGIN("40100", HttpStatus.UNAUTHORIZED, "인증에 실패했습니다"),

    // Not Found Error
    NOT_FOUND_USER("40400", HttpStatus.NOT_FOUND, "해당 사용자가 존재하지 않습니다."),
    NOT_FOUND_END_POINT("40401", HttpStatus.NOT_FOUND, "존재하지 않는 엔드포인트입니다."),
    NOT_FOUND_RESOURCE("40402", HttpStatus.NOT_FOUND, "요청한 데이터를 찾을 수 없습니다."),
    NOT_FOUND_POPUP("40403", HttpStatus.NOT_FOUND, "해당 팝업이 존재하지 않습니다."),

    // Access Denied Error
    ACCESS_DENIED_ERROR("40300", HttpStatus.FORBIDDEN, "액세스 권한이 없습니다."),

    // Server, File Up/DownLoad Error
    SERVER_ERROR("50000", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
