package com.poppin.poppinserver.oauth;


public record OAuth2UserInfo (
        String uniqueId,    // 구글/애플 sub, 카카오 id, 네이버 response안의 id
        String email
) {
    public static OAuth2UserInfo of(String uniqueId, String email) {
        return new OAuth2UserInfo(uniqueId, email);
    }
}
