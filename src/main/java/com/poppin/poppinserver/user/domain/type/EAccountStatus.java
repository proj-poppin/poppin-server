package com.poppin.poppinserver.user.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EAccountStatus {
    LOGIN("로그인"),
    SIGNUP("회원가입"),
    UNAVAILABLE("로그인 불가");

    private final String status;
}
