package com.poppin.poppinserver.user.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.domain.type.EUserRole;
import java.util.Optional;

@UseCase
public interface UserQueryUseCase {
    // 유저 PK로 조회 메서드
    User findUserById(Long userId);

    // 유저 이메일로 조회 메서드
    User findUserByEmail(String email);

    // 유저 이메일로 조회 메서드 (Optional)
    Optional<User> findUserByEmailOptional(String email);

    // 유저 존재 확인 메서드
    Boolean existsById(Long userId);

    // 유저 중복 이메일 확인 메서드
    void checkDuplicatedEmail(String email);

    // 유저 비밀번호 및 비밀번호 확인 일치 여부 검증 메서드
    void checkPasswordMatch(String password, String passwordConfirm);

    // 유저 닉네임 중복 확인 메서드
    void checkDuplicatedNickname(String nickname);

    // 유저 이메일과 권한으로 조회 메서드
    User findUserByEmailAndRole(String email, EUserRole role);

    // 유저 로그인 시 비밀번호 일치 여부 확인 메서드
    // void checkPasswordMatchForLogin(String inputPassword, String userPassword);
}
