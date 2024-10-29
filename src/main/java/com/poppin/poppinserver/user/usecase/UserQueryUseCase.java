package com.poppin.poppinserver.user.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.user.domain.User;

@UseCase
public interface UserQueryUseCase {
    // 유저 이메일 중복 확인 메서드
    User findUserById(Long userId);

    // 유저 존재 확인 메서드
    Boolean existsById(Long userId);

    // 유저 비밀번호 및 비밀번호 확인 일치 여부 검증 메서드
    void checkDuplicatedEmail(String email);

    // 유저 비밀번호 및 비밀번호 확인 일치 여부 검증 메서드
    void checkPasswordMatch(String password, String passwordConfirm);

    // 유저 닉네임 중복 확인 메서드
    void checkDuplicatedNickname(String nickname);
}
