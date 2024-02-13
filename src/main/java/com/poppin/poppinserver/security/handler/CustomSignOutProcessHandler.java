package com.poppin.poppinserver.security.handler;

import com.poppin.poppinserver.repository.UserRepository;
import com.poppin.poppinserver.security.info.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomSignOutProcessHandler implements LogoutHandler {
    private final UserRepository userRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication == null) {
            return;
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        userRepository.updateRefreshTokenAndLoginStatus(userDetails.getEmail(), null, false);
    }
}
