package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.dto.request.AuthSignUpDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void authSignUp(AuthSignUpDto authSignUpDto) {
        // 유저 중복 확인
        userRepository.findByEmail(authSignUpDto.email())
                .ifPresent(user -> {
                    throw new CommonException(ErrorCode.DUPLICATED_SERIAL_ID);
                });
        // 유저 생성, 패스워드 암호화
        userRepository.save(User.toUserEntity(authSignUpDto, bCryptPasswordEncoder.encode(authSignUpDto.password())));
    }
}
