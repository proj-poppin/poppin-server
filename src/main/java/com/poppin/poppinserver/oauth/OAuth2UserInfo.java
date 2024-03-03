package com.poppin.poppinserver.oauth;


public record OAuth2UserInfo (
        String email
) {
    public static OAuth2UserInfo of(String email) {
        return new OAuth2UserInfo(email);
    }
}
