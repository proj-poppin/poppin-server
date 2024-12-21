package com.poppin.poppinserver.alarm.service;

import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.repository.FCMTokenRepository;
import com.poppin.poppinserver.alarm.usecase.TokenQueryUseCase;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.user.domain.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenQueryService implements TokenQueryUseCase {

    private final FCMTokenRepository fcmTokenRepository;

    @Override
    public Optional<FCMToken> findTokenByUserId(Long userId) {
        return fcmTokenRepository.findByUserId(userId);
    }

    @Override
    public FCMToken findByToken(String token) {
        FCMToken pushToken = fcmTokenRepository.findByToken(token);
        if (pushToken == null) {
            throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);
        }
        return pushToken;
    }

    @Override
    public void verifyToken(String token) {
        fcmTokenRepository.findByTokenOpt(token)
                .ifPresent(fcmToken -> {
                    throw new CommonException(ErrorCode.ALREADY_EXIST_FCM_TOKEN);
                });
    }


    @Override
    public FCMToken findByUser(User user) {
        return fcmTokenRepository.findByUser(user);
    }

    @Override
    public List<FCMToken> findAll() {
        return fcmTokenRepository.findAll();
    }

}
