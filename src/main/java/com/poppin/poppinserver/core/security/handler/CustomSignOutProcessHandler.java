package com.poppin.poppinserver.core.security.handler;

import com.poppin.poppinserver.core.security.info.CustomUserDetails;
import com.poppin.poppinserver.user.repository.UserCommandRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomSignOutProcessHandler implements LogoutHandler {
    private final UserCommandRepository userCommandRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        userCommandRepository.updateRefreshTokenAndLoginStatus(userDetails.getId(), null, false);
    }
}
