package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.domain.type.EUserRole;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService implements UserQueryUseCase {
    private final UserQueryRepository userQueryRepository;

    // User PK로 유저 조회 메서드
    @Override
    public User findUserById(Long userId) {
        return userQueryRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
    }

    // 이메일로 유저 조회 메서드
    @Override
    public User findUserByEmail(String email) {
        return userQueryRepository.findByEmail(email)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
    }

    @Override
    public Optional<User> findUserByEmailOptional(String email) {
        return userQueryRepository.findByEmail(email);
    }

    // User PK로 유저 존재 여부 조회 메서드
    @Override
    public Boolean existsById(Long userId) {
        return userQueryRepository.existsById(userId);
    }

    // 유저 이메일 중복 확인 메서드
    @Override
    public void checkDuplicatedEmail(String email) {
        if (userQueryRepository.existsByEmail(email)) {
            throw new CommonException(ErrorCode.DUPLICATED_EMAIL);
        }
    }

    // 유저 비밀번호 및 비밀번호 확인 일치 여부 검증 메서드
    @Override
    public void checkPasswordMatch(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new CommonException(ErrorCode.PASSWORD_NOT_MATCH);
        }
    }

    // 유저 닉네임 중복 확인 메서드
    @Override
    public void checkDuplicatedNickname(String nickname) {
        if (userQueryRepository.existsByNickname(nickname)) {
            throw new CommonException(ErrorCode.DUPLICATED_NICKNAME);
        }
    }

    // 유저 이메일과 권한으로 조회 메서드
    @Override
    public User findUserByEmailAndRole(String email, EUserRole role) {
        return userQueryRepository.findByEmailAndRole(email, role);
    }
}
