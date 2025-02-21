package com.poppin.poppinserver.core.constant;


import java.util.List;

public class Constants {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String BASIC_PREFIX = "Basic ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REAUTHORIZATION = "refreshToken";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String USER_ROLE_CLAIM_NAME = "role";
    public static final String USER_ID_CLAIM_NAME = "uid";
    public static final String USER_EMAIL_CLAIM_NAME = "email";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final Long MEMBER_INFO_RETENTION_PERIOD = 30L;

    // 소셜 로그인 관련 상수
    public static final String KAKAO_RESOURCE_SERVER_URL = "https://kapi.kakao.com/v2/user/me";
    public static final String NAVER_RESOURCE_SERVER_URL = "https://openapi.naver.com/v1/nid/me";
    public static final String GOOGLE_RESOURCE_SERVER_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
    public static final String APPLE_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys";


    public static final List<String> NO_NEED_AUTH_URLS = List.of(
            "/api/v1/auth/sign-up",                                // 회원가입
            "/api/v1/popup/hot-list",                                       // 인기 팝업 목록 조회
            "/api/v1/popup/new-list",                                       // 새로 오픈 팝업 목록 조회
            "/api/v1/popup/closing-list",                                   // 종료 임박 팝업 목록 조회
            "/api/v1/popup/guest/detail",                                   // 팝업 비로그인 상세 조회
            "/api/v1/popup/search",                                   // 비로그인 팝업 검색
            "/api/v1/popup/guest/search/base",                              // 비로그인 팝업 베이스 검색

            // 로그인
            "/api/v1/auth/sign-in",
            "/api/v1/auth/login/**",
            "/api/v1/auth/login/kakao",
            "/api/v1/auth/login/naver",
            "/api/v1/auth/login/google",
            "/api/v1/auth/login/apple",

            // 계정 상태 확인
            "/api/v1/auth/account/status",
            "/api/v1/auth/account/status/apple",

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
            "/api/v1/constants",                                             // 앱 시작 시 버전 확인
            "/api/v1/popup/detail/**",

            //스웨거
            "/swagger",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/api-docs",
            "/api-docs/**",
            "/v3/api-docs/**"
    );

    public static final String DEFAULT_POSTER = "https://poppin-local-test.s3.ap-northeast-2.amazonaws.com/default/poppin.png";

    /**
     * 앱 버전 관련 상수
     */
    public static final String RECENT_VERSION = "1.4.2";
    public static final String REQUIRED_VERSION = "1.4.2";
    public static final String REQUIRED_IOS_VERSION = "1.4.2";
    public static final String REQUIRED_ANDROID_VERSION = "1.4.2";

    /**
     * 앱 다운로드 URL
     */
    public static final String APPLE_APP_STORE_URL = "https://apps.apple.com/kr/app/%ED%8C%9D%ED%95%80-%EB%A7%9E%EC%B6%A4%ED%98%95-%ED%8C%9D%EC%97%85-%EC%8A%A4%ED%86%A0%EC%96%B4-%EC%B6%94%EC%B2%9C/id6482994685";

    /**
     * 약관 관련 URL
     */
    public static final String SERVICE_TERMS = "https://docs.google.com/document/d/1gFo_QEY_lea3pzP9fJH9X0oWx7yF6PgefenNWsJDMvM/edit?usp=sharing";
    public static final String PRIVACY_TERMS = "https://docs.google.com/document/d/1KgYNHqbleQ3r9lbhuVjbCDEpxW-zPzDKka8D2qVZ2UI/edit?usp=sharing";


}
