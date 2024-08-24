package com.poppin.poppinserver.user.oauth.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoAccount(
        @JsonProperty("profile") KakaoUserProfile kakaoUserProfile,
        String email
) {
}
