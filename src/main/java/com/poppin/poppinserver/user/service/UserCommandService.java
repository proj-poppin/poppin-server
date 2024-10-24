package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.core.util.PasswordUtil;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.domain.type.ELoginProvider;
import com.poppin.poppinserver.user.dto.auth.request.AuthSignUpRequestDto;
import com.poppin.poppinserver.user.repository.UserCommandRepository;
import com.poppin.poppinserver.user.usecase.UserCommandUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCommandService implements UserCommandUseCase {
    private final UserCommandRepository userCommandRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User createUserByDefaultSignUp(AuthSignUpRequestDto authSignUpRequestDto) {
        return userCommandRepository.save(
                User.toUserEntity(
                        authSignUpRequestDto,
                        bCryptPasswordEncoder.encode(authSignUpRequestDto.password()),
                        ELoginProvider.DEFAULT
                )
        );
    }

    @Override
    public User createUserBySocialSignUp(AuthSignUpRequestDto authSignUpRequestDto) {
        return userCommandRepository.save(
                User.toUserEntity(
                        authSignUpRequestDto,
                        bCryptPasswordEncoder.encode(PasswordUtil.generateRandomPassword()),
                        ELoginProvider.valueOf(authSignUpRequestDto.accountType())
                )
        );
    }
}
