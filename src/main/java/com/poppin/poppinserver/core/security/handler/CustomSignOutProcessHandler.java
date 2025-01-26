package com.poppin.poppinserver.core.security.handler;

import com.poppin.poppinserver.alarm.repository.FCMTokenRepository;
import com.poppin.poppinserver.core.security.info.CustomUserDetails;
import com.poppin.poppinserver.user.repository.UserCommandRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class CustomSignOutProcessHandler implements LogoutHandler {
    private final UserCommandRepository userCommandRepository;
    private final FCMTokenRepository fcmTokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        processSignOut(userDetails.getId());
    }

    protected void processSignOut(Long userId) {
        userCommandRepository.updateRefreshToken(userId, null); // RefreshToken 삭제
        log.info("User {} sign out", userId);
        fcmTokenRepository.findByUserId(userId).ifPresent(fcmTokenRepository::delete);  // FCMToken 삭제
    }
}
