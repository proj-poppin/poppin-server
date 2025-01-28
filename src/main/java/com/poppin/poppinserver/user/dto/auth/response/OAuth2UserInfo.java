package com.poppin.poppinserver.user.dto.auth.response;


public record OAuth2UserInfo(
        String oAuthId,    // 구글/애플 sub, 카카오 id, 네이버 response안의 id
        String email
) {
    public static OAuth2UserInfo of(String oAuthId, String email) {
        return new OAuth2UserInfo(oAuthId, email);
    }
}
