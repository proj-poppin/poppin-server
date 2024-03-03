package com.poppin.poppinserver.oauth.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoAccount(
        @JsonProperty("profile") KakaoUserProfile kakaoUserProfile,
        String email
) {
}
