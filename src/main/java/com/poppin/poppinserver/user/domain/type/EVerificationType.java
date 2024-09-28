package com.poppin.poppinserver.user.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EVerificationType {
    PASSWORD_RESET("비밀번호 재설정"),
    SIGN_UP("회원가입");

    private final String type;
}
