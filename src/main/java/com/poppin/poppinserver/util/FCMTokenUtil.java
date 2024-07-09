package com.poppin.poppinserver.util;

import com.poppin.poppinserver.domain.FCMToken;
import com.poppin.poppinserver.repository.FCMTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FCMTokenUtil {

    private static ApplicationContext context;


    public static void refreshToken(FCMToken token) {
        FCMTokenRepository fcmTokenRepository = context.getBean(FCMTokenRepository.class);

        log.info("refresh token : " + token.getToken());

        token.regenerateToken();

        fcmTokenRepository.save(token);
    }
}
