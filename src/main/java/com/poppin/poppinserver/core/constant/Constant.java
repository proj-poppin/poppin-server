package com.poppin.poppinserver.core.constant;


import java.util.List;

public class Constant {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String BASIC_PREFIX = "Basic ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION = "accessToken";
    public static final String REAUTHORIZATION = "refreshToken";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String USER_ROLE_CLAIM_NAME = "role";
    public static final String USER_ID_CLAIM_NAME = "uid";
    public static final String USER_EMAIL_CLAIM_NAME = "email";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final Long MEMBER_INFO_RETENTION_PERIOD = 30L;

    // 앱 버전 및 OS
    public static final String iOS_APP_VERSION = "1.0.4";
    public static final String iOS = "ios";
    public static final String ANDROID_APP_VERSION = "1.0.0";
    public static final String ANDROID = "android";

    public static final List<String> NO_NEED_AUTH_URLS = List.of(
            "/api/v1/auth/sign-up",                                // 회원가입
            "/api/v1/popup/hot-list",                                       // 인기 팝업 목록 조회
            "/api/v1/popup/new-list",                                       // 새로 오픈 팝업 목록 조회
            "/api/v1/popup/closing-list",                                   // 종료 임박 팝업 목록 조회
            "/api/v1/popup/guest/detail",                                   // 팝업 비로그인 상세 조회
            "/api/v1/popup/search",                                   // 비로그인 팝업 검색
            "/api/v1/popup/guest/search/base",                              // 비로그인 팝업 베이스 검색
            "/api/v1/manager-inform/guest",                                 // 비로그인 운영자 팝업 제보
            "/api/v1/user-inform/guest/report",                             // 비로그인 이용자 팝업 제보

            // 로그인
            "/api/v1/auth/sign-in",
            "/api/v1/auth/login/kakao",
            "/api/v1/auth/login/naver",
            "/api/v1/auth/login/google",
            "/api/v1/auth/login/apple",

            // 이메일 인증
            "/api/v1/auth/email/verification",
            "/api/v1/auth/email/verification/password",

            // 비밀번호 분실 시 변경
            "/api/v1/auth/reset-password/no-auth",

            "/api/v1/noti/token/test",                                      // fcm 토큰 테스트
            "/api/v1/noti/topic/test",                                      // fcm 토픽 테스트
            "/api/v1/noti/apply/FCMtoken",                                  // fcm 토큰 등록

            "/api/v1/users/support/faqs",
            "/api/v1/users/random-nickname",

            "/api/v1/alarm/unread",                                         // 공지사항 알림 읽음 여부
            "/api/v1/alarm/info",                                           // 공지사항 알림 1 depth
            "/api/v1/alarm/info/detail",                                    // 공지사항 알림 2 depth

            "/api/v1/alarm/popup",                                          // 팝업 알림 1 depth
            "/api/v1/alarm/popup/guest/detail",                              // 팝업 알림 2 depth
            "/api/v1/admin/sign-in",                                         // 관리자 로그인

            "/api/v1/bootstrap",                                             // 부트스트랩
            "/api/v1/app/start",                                             // 앱 시작 시 버전 확인
            "/api/v1/popup/detail/**"
    );

    public static final String DEFAULT_POSTER = "https://poppin-local-test.s3.ap-northeast-2.amazonaws.com/default/poppin.png";
}
