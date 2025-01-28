package com.poppin.poppinserver.user.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ELoginProvider {
    GOOGLE("GOOGLE"),
    KAKAO("KAKAO"),
    NAVER("NAVER"),
    APPLE("APPLE"),
    DEFAULT("DEFAULT");

    private final String loginProvider;
}
