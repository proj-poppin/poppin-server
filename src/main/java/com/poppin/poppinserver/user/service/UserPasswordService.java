package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.dto.auth.request.PasswordResetRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.PasswordUpdateRequestDto;
import com.poppin.poppinserver.user.dto.auth.request.PasswordVerificationRequestDto;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPasswordService {
    private final UserQueryRepository userQueryRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 인증 없이 비밀번호 재설정 메서드 - 유저가 비밀번호를 잊어버렸을 때
    @Transactional
    public void resetPasswordNoAuth(PasswordResetRequestDto passwordResetRequestDto) {
        User user = userQueryRepository.findByEmail(passwordResetRequestDto.email())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        if (!passwordResetRequestDto.password().equals(passwordResetRequestDto.passwordConfirm())) {
            throw new CommonException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        user.updatePassword(bCryptPasswordEncoder.encode(passwordResetRequestDto.password()));
    }

    // 비밀번호 재설정 메서드
    @Transactional
    public void resetPassword(Long userId, PasswordUpdateRequestDto passwordRequestDto) {
        User user = userQueryRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        // 비밀번호와 비밀번호 확인 일치 여부 검증
        checkPasswordMatch(passwordRequestDto.password(), passwordRequestDto.passwordConfirm());

        // 기존 쓰던 비밀번호로 설정해도 무방
        user.updatePassword(bCryptPasswordEncoder.encode(passwordRequestDto.password()));
    }

    // 유저 비밀번호 및 비밀번호 확인 일치 여부 검증 메서드
    private void checkPasswordMatch(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new CommonException(ErrorCode.PASSWORD_NOT_MATCH);
        }
    }

    // 유저 비밀번호를 받아서 비밀번호 확인 메서드
    @Transactional(readOnly = true)
    public Boolean verifyPassword(Long userId, PasswordVerificationRequestDto passwordVerificationRequestDto) {
        User user = userQueryRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        if (!bCryptPasswordEncoder.matches(passwordVerificationRequestDto.password(), user.getPassword())) {
            throw new CommonException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        return Boolean.TRUE;
    }
}
