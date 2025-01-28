package com.poppin.poppinserver.alarm.usecase;

import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.user.domain.User;
import java.util.List;
import java.util.Optional;

@UseCase
public interface TokenQueryUseCase {

    Optional<FCMToken> findTokenByUserId(Long userId);

    FCMToken findByToken(String token);

    void verifyToken(String token);

    FCMToken findByUser(User user);

    List<FCMToken> findAll();

    User findUserByToken(FCMToken token);
}
