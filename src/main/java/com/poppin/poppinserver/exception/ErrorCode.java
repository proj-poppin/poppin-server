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
    INVALID_ROLE("40002", HttpStatus.BAD_REQUEST, "유효하지 않은 권한입니다."),
    INVALID_PROVIDER("40003", HttpStatus.BAD_REQUEST, "유효하지 않은 제공자입니다."),
    INVALID_HEADER("40004", HttpStatus.BAD_REQUEST, "유효하지 않은 헤더값입니다."),
    DUPLICATED_SERIAL_ID("40005", HttpStatus.BAD_REQUEST, "중복된 아이디입니다."),
    PASSWORD_NOT_MATCH("40006", HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    DUPLICATED_NICKNAME("40007", HttpStatus.BAD_REQUEST, "중복된 닉네임입니다."),
    MISSING_REQUEST_BODY("40008", HttpStatus.BAD_REQUEST, "요청 바디가 누락되었습니다."),
    DUPLICATED_INTEREST("40009", HttpStatus.BAD_REQUEST, "이미 관심 등록된 팝업입니다."),
    MISSING_REQUEST_IMAGES("40010", HttpStatus.BAD_REQUEST, "이미지가 누락되었습니다."),
    INVALID_APPLE_IDENTITY_TOKEN_ERROR("40011", HttpStatus.BAD_REQUEST, "유효하지 않은 Apple Identity Token입니다."),
    EXPIRED_APPLE_IDENTITY_TOKEN_ERROR("40012", HttpStatus.BAD_REQUEST, "만료된 Apple Identity Token입니다."),
    INVALID_APPLE_PUBLIC_KEY_ERROR("40013", HttpStatus.BAD_REQUEST, "유효하지 않은 Apple Public Key입니다."),
    PASSWORD_SAME("40014", HttpStatus.BAD_REQUEST, "기존 비밀번호와 동일합니다."),
    DUPLICATED_REALTIMEVISIT("40015" , HttpStatus.BAD_REQUEST, "30분 이내 방문하기 버튼을 2번 이상 누르실 수 없습니다."),
    ALREADY_EXISTS_PREFERENCE("40016", HttpStatus.BAD_REQUEST, "이미 취향 정보가 존재합니다."),
    INVALID_DATE_PARAMETER("40017", HttpStatus.BAD_REQUEST, "종료 날짜가 오픈 날짜보다 빠를 수 없습니다."),
    MAIL_SEND_ERROR("40018", HttpStatus.BAD_REQUEST, "메일 전송에 실패하였습니다."),

    // Unauthorized Error
    FAILURE_LOGIN("40100", HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다."),
    FAILURE_LOGOUT("40101", HttpStatus.UNAUTHORIZED, "로그아웃에 실패했습니다."),
    INVALID_TOKEN_ERROR("40102", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN_ERROR("40103", HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    TOKEN_MALFORMED_ERROR("40104", HttpStatus.UNAUTHORIZED, "토큰이 올바르지 않습니다."),
    TOKEN_TYPE_ERROR("40105", HttpStatus.UNAUTHORIZED, "토큰 타입이 일치하지 않습니다."),
    TOKEN_UNSUPPORTED_ERROR("40106", HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰입니다."),
    TOKEN_GENERATION_ERROR("40107", HttpStatus.UNAUTHORIZED, "토큰 생성에 실패하였습니다."),
    TOKEN_UNKNOWN_ERROR("40108", HttpStatus.UNAUTHORIZED, "알 수 없는 토큰입니다."),
    EMPTY_AUTHENTICATION("40109", HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다."),

    // Access Denied Error
    ACCESS_DENIED_ERROR("40300", HttpStatus.FORBIDDEN, "액세스 권한이 없습니다."),

    // Not Found Error
    NOT_FOUND_USER("40400", HttpStatus.NOT_FOUND, "해당 사용자가 존재하지 않습니다."),
    NOT_FOUND_END_POINT("40401", HttpStatus.NOT_FOUND, "존재하지 않는 엔드포인트입니다."),
    NOT_FOUND_RESOURCE("40402", HttpStatus.NOT_FOUND, "요청한 데이터를 찾을 수 없습니다."),
    NOT_FOUND_POPUP("40403", HttpStatus.NOT_FOUND, "해당 팝업이 존재하지 않습니다."),
    NOT_FOUND_REVIEW("40404" , HttpStatus.NOT_FOUND, "해당 리뷰가 존재하지 않습니다."),
    NOT_FOUND_VISITOR_DATA("40405", HttpStatus.NOT_FOUND, "해당 방문자 데이터가 존재하지 않습니다."),
    NOT_FOUND_REALTIMEVISIT("40406", HttpStatus.NOT_FOUND, "방문자 데이터가 존재하지 않습니다"),
    NOT_FOUND_USER_TASTE("40407", HttpStatus.NOT_FOUND, "해당 사용자의 설정 취향이 존재하지 않습니다."),
    NOT_FOUND_USER_PREFER("40408", HttpStatus.NOT_FOUND, "해당 사용자의 설정 선호도가 존재하지 않습니다."),
    NOT_FOUND_USER_WHOWITH("40409", HttpStatus.NOT_FOUND, "해당 사용자의 설정 동반인이 존재하지 않습니다."),


    // UnsupportedMediaType Error
    UNSUPPORTED_MEDIA_TYPE("41500", HttpStatus.UNSUPPORTED_MEDIA_TYPE, "허용되지 않은 파일 형식입니다."),

    // Server, File Up/DownLoad Error
    SERVER_ERROR("50000", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
