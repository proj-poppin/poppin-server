package com.poppin.poppinserver.core.util;

import com.poppin.poppinserver.alarm.domain.FCMToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FCMRefreshUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static void refreshToken(FCMToken fcmToken) {
        log.info("refresh token : " + fcmToken.getToken());
        fcmToken.refreshToken();
    }
}
