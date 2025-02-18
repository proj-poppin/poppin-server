package com.poppin.poppinserver.core.exception;

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
    DUPLICATED_EMAIL("40005", HttpStatus.BAD_REQUEST, "해당 이메일로 가입된 계정이 존재합니다."),
    PASSWORD_NOT_MATCH("40006", HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    DUPLICATED_NICKNAME("40007", HttpStatus.BAD_REQUEST, "중복된 닉네임입니다."),
    MISSING_REQUEST_BODY("40008", HttpStatus.BAD_REQUEST, "요청 바디가 누락되었습니다."),
    DUPLICATED_INTEREST("40009", HttpStatus.BAD_REQUEST, "이미 관심 등록된 팝업입니다."),
    MISSING_REQUEST_IMAGES("40010", HttpStatus.BAD_REQUEST, "이미지가 누락되었습니다."),
    INVALID_APPLE_IDENTITY_TOKEN_ERROR("40011", HttpStatus.BAD_REQUEST, "유효하지 않은 Apple Identity Token입니다."),
    EXPIRED_APPLE_IDENTITY_TOKEN_ERROR("40012", HttpStatus.BAD_REQUEST, "만료된 Apple Identity Token입니다."),
    INVALID_APPLE_PUBLIC_KEY_ERROR("40013", HttpStatus.BAD_REQUEST, "유효하지 않은 Apple Public Key입니다."),
    PASSWORD_SAME("40014", HttpStatus.BAD_REQUEST, "기존 비밀번호와 동일합니다."),
    DUPLICATED_REALTIME_VISIT("40015", HttpStatus.BAD_REQUEST, "방문하기 버튼을 2번 이상 누르실 수 없습니다."),
    ALREADY_EXISTS_PREFERENCE("40016", HttpStatus.BAD_REQUEST, "이미 취향 정보가 존재합니다."),
    INVALID_DATE_PARAMETER("40017", HttpStatus.BAD_REQUEST, "종료 날짜가 오픈 날짜보다 빠를 수 없습니다."),
    MAIL_SEND_ERROR("40018", HttpStatus.BAD_REQUEST, "메일 전송에 실패하였습니다."),
    INVALID_LOGIN("40019", HttpStatus.BAD_REQUEST, "로그인 정보가 올바르지 않습니다."),
    DUPLICATED_RECOMMEND_COUNT("40020", HttpStatus.BAD_REQUEST, "같은 후기에 추천을 2번 이상 하실 수 없습니다."),
    INVALID_OAUTH2_PROVIDER("40021", HttpStatus.BAD_REQUEST, "유효하지 않은 OAuth2 제공자입니다."),
    INVALID_CATEGORY_REQUEST("40022", HttpStatus.BAD_REQUEST, "prepered 코드는 3개, taste 코드는 14개 이상이어야 합니다."),
    DUPLICATED_TOKEN("40023", HttpStatus.BAD_REQUEST, "이미 알림 동의를 하셨습니다."),
    DELETED_USER_ERROR("40024", HttpStatus.BAD_REQUEST, "탈퇴한 유저는 30일 동안 재가입할 수 없습니다."),
    REVIEW_RECOMMEND_ERROR("40025", HttpStatus.BAD_REQUEST, "자신의 후기에 추천 할 수 없습니다."),
    DUPLICATED_SOCIAL_ID("40026", HttpStatus.BAD_REQUEST, "해당 이메일로 가입된 소셜 계정이 존재합니다."),
    CANNOT_BLOCK_MYSELF("40027", HttpStatus.BAD_REQUEST, "자신을 차단할 수 없습니다."),
    ALREADY_BLOCKED_USER("40028", HttpStatus.BAD_REQUEST, "이미 차단된 사용자입니다."),
    DUPLICATED_ALARM_KEYWORD("40029", HttpStatus.BAD_REQUEST, "이미 등록된 알람 키워드입니다."),
    NOT_FOUND_ALARM_KEYWORD("40030", HttpStatus.BAD_REQUEST, "존재하지 않는 알람 키워드입니다."),
    ALREADY_EXIST_FCM_TOKEN("40031", HttpStatus.BAD_REQUEST, "중복된 FCM 토큰입니다."),
    INVALID_CATEGORY_STRING("40032", HttpStatus.BAD_REQUEST, "유효한 카테고리를 하나 이상 요청 해야 합니다."),
    DUPLICATED_REVIEW("40033", HttpStatus.BAD_REQUEST, "팝업 스토어에는 하나의 후기만 작성 가능 합니다."),
    ALARM_CHECK("40034", HttpStatus.BAD_REQUEST, "알림 읽음 요청이 실패 하였습니다."),
    INVALID_THREE_CATEGORY("40035", HttpStatus.BAD_REQUEST,
            "filteringThreeCategories는 빈 문자열 이거나 market,display,experience와 같은 형식이어야 합니다."),
    INVALID_FOURTEEN_CATEGORY("40036", HttpStatus.BAD_REQUEST,
            "filteringFourteenCategories는 빈 문자열 이거나 fashionBeauty,characters,foodBeverage,webtoonAni,interiorThings,movie,musical,sports,game,itTech,kpop,alcohol,animalPlant,etc와 같은 형식이어야 합니다."),
    ALREADY_WRITTEN_REVIEW("40037", HttpStatus.BAD_REQUEST, "이미 후기를 작성한 팝업에는 방문하실 수 없습니다."),

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
    INVALID_AUTHORIZATION_HEADER("40110", HttpStatus.UNAUTHORIZED, "유효하지 않은 인증 헤더입니다."),


    // Access Denied Error
    ACCESS_DENIED_ERROR("40300", HttpStatus.FORBIDDEN, "액세스 권한이 없습니다."),

    // Not Found Error
    NOT_FOUND_USER("40400", HttpStatus.NOT_FOUND, "해당 사용자가 존재하지 않습니다."),
    NOT_FOUND_END_POINT("40401", HttpStatus.NOT_FOUND, "존재하지 않는 엔드포인트입니다."),
    NOT_FOUND_RESOURCE("40402", HttpStatus.NOT_FOUND, "요청한 데이터를 찾을 수 없습니다."),
    NOT_FOUND_POPUP("40403", HttpStatus.NOT_FOUND, "해당 팝업이 존재하지 않습니다."),
    NOT_FOUND_REVIEW("40404", HttpStatus.NOT_FOUND, "해당 리뷰가 존재하지 않습니다."),
    NOT_FOUND_VISITOR_DATA("40405", HttpStatus.NOT_FOUND, "해당 방문자 데이터가 존재하지 않습니다."),
    NOT_FOUND_REALTIMEVISIT("40406", HttpStatus.NOT_FOUND, "방문자 데이터가 존재하지 않습니다"),
    NOT_FOUND_USER_TASTE("40407", HttpStatus.NOT_FOUND, "해당 사용자의 설정 취향이 존재하지 않습니다."),
    NOT_FOUND_USER_PREFER("40408", HttpStatus.NOT_FOUND, "해당 사용자의 설정 선호도가 존재하지 않습니다."),
    NOT_FOUND_USER_WHOWITH("40409", HttpStatus.NOT_FOUND, "해당 사용자의 설정 동반인이 존재하지 않습니다."),
    NOT_FOUND_USER_INFORM("40410", HttpStatus.NOT_FOUND, "해당 사용자 제보는 존재하지 않습니다."),
    NOT_FOUND_MANAGE_INFORM("40411", HttpStatus.NOT_FOUND, "해당 사용자 제보는 존재하지 않습니다."),
    NOT_FOUND_MODIFY_INFO("40412", HttpStatus.NOT_FOUND, "해당 정보수정요청 정보는 존재하지 않습니다."),
    NOT_FOUND_VISIT("40413", HttpStatus.NOT_FOUND, "방문한 내역이 없습니다"),
    NOT_FOUND_TOKEN("40414", HttpStatus.NOT_FOUND, "알림 토큰이 저장된 내역이 없습니다"),
    NOT_FOUND_TOPIC("40415", HttpStatus.NOT_FOUND, "알림 토픽이 저장된 내역이 없습니다"),
    NOT_FOUND_ALARM_SETTING("40416", HttpStatus.NOT_FOUND, "알림 세팅 내역이 없습니다"),
    NOT_FOUND_INFO_ALARM("40417", HttpStatus.NOT_FOUND, "공지사항 알림 상세 내역이 없습니다"),
    NOT_FOUND_POPUP_ALARM("40418", HttpStatus.NOT_FOUND, "팝업 알림 상세 내역이 없습니다"),
    NOT_FOUND_INFO_IMG("40419", HttpStatus.NOT_FOUND, "공지사항 상세 이미지를 찾을 수 없습니다"),
    NOT_FOUND_POPUP_REPORT("40420", HttpStatus.NOT_FOUND, "해당 팝업 신고 내역을 찾을 수 없습니다."),
    NOT_FOUND_REVIEW_REPORT("40421", HttpStatus.NOT_FOUND, "해당 리뷰 신고 내역을 찾을 수 없습니다."),
    NOT_FOUND_FCM_TOKEN("40422", HttpStatus.NOT_FOUND, "해당 유저의 fcm 토큰을 찾을 수 없습니다."),
    NOT_FOUND_INTEREST("40423", HttpStatus.NOT_FOUND, "해당 관심 저장 정보를 찾을 수 없습니다."),
    NOT_FOUND_ALARM("40424", HttpStatus.NOT_FOUND, "알림 정보를 찾을 수 없습니다."),
    NOT_FOUND_ALARM_TYPE("40425", HttpStatus.NOT_FOUND, "알림 타입을 확인할 수 없습니다."),

    // UnsupportedMediaType Error
    UNSUPPORTED_MEDIA_TYPE("41500", HttpStatus.UNSUPPORTED_MEDIA_TYPE, "허용되지 않은 파일 형식입니다."),

    // Server, File Up/DownLoad Error
    SERVER_ERROR("50000", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    FCM_ERROR("50001", HttpStatus.INTERNAL_SERVER_ERROR, "공지사항 등록 중 앱 푸시 알림 오류입니다."),
    REVIEW_FCM_ERROR("50002", HttpStatus.INTERNAL_SERVER_ERROR, "후기 생성 중 오류가 발생하였습니다:fcm 오류"),
    INFO_ALARM_ERROR("50003", HttpStatus.INTERNAL_SERVER_ERROR, "공지사항 등록 과정 중 알림 등록 오류 발생하였습니다");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
