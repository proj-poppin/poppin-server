package com.poppin.poppinserver.oauth.kakao;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserInfo(
        Long id,
        KakaoAccount kakaoAccount
) {
}
