package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.core.util.PasswordUtil;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.domain.type.ELoginProvider;
import com.poppin.poppinserver.user.domain.type.EUserRole;
import com.poppin.poppinserver.user.dto.auth.request.AuthSignUpRequestDto;
import com.poppin.poppinserver.user.repository.UserCommandRepository;
import com.poppin.poppinserver.user.usecase.UserCommandUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandService implements UserCommandUseCase {
    private final UserCommandRepository userCommandRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User createUserByDefaultSignUp(AuthSignUpRequestDto authSignUpRequestDto) {
        String email = authSignUpRequestDto.email();
        String password = authSignUpRequestDto.password();
        String nickname = authSignUpRequestDto.nickname();
        String accountType = authSignUpRequestDto.accountType();
        Boolean agreedToPrivacyPolicy = authSignUpRequestDto.agreedToPrivacyPolicy();
        Boolean agreedToServiceTerms = authSignUpRequestDto.agreedToServiceTerms();
        return userCommandRepository.save(
                User.builder()
                        .email(email)
                        .password(bCryptPasswordEncoder.encode(password))
                        .nickname(nickname)
                        .eLoginProvider(ELoginProvider.valueOf(accountType))
                        .role(EUserRole.USER)
                        .agreedToPrivacyPolicy(agreedToPrivacyPolicy)
                        .agreedToServiceTerms(agreedToServiceTerms)
                        .build()
        );
    }

    @Override
    public User createUserBySocialSignUp(AuthSignUpRequestDto authSignUpRequestDto) {
        String appleUserId = authSignUpRequestDto.appleUserId();
        String email = authSignUpRequestDto.email();
        String nickname = authSignUpRequestDto.nickname();
        String accountType = authSignUpRequestDto.accountType();
        Boolean agreedToPrivacyPolicy = authSignUpRequestDto.agreedToPrivacyPolicy();
        Boolean agreedToServiceTerms = authSignUpRequestDto.agreedToServiceTerms();
        if (appleUserId != null) {
            return userCommandRepository.save(
                    User.builder()
                            .email(appleUserId)
                            .password(bCryptPasswordEncoder.encode(PasswordUtil.generateRandomPassword()))
                            .nickname(authSignUpRequestDto.nickname())
                            .eLoginProvider(ELoginProvider.APPLE)
                            .role(EUserRole.USER)
                            .agreedToPrivacyPolicy(agreedToPrivacyPolicy)
                            .agreedToServiceTerms(agreedToServiceTerms)
                            .build()
            );
        }
        return userCommandRepository.save(
                User.builder()
                        .email(email)
                        .password(bCryptPasswordEncoder.encode(PasswordUtil.generateRandomPassword()))
                        .nickname(nickname)
                        .eLoginProvider(ELoginProvider.valueOf(accountType))
                        .role(EUserRole.USER)
                        .agreedToPrivacyPolicy(agreedToPrivacyPolicy)
                        .agreedToServiceTerms(agreedToServiceTerms)
                        .build()
        );
    }
}
