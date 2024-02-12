package com.poppin.poppinserver.security.handler;

import com.poppin.poppinserver.dto.auth.response.JwtTokenDto;
import com.poppin.poppinserver.repository.UserRepository;
import com.poppin.poppinserver.security.info.CustomUserDetails;
import com.poppin.poppinserver.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONValue;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class DefaultSignInSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        JwtTokenDto jwtTokenDto = jwtUtil.generateToken(userDetails.getEmail(), userDetails.getRole());
        // String userAgent = request.getHeader("User-Agent");

        userRepository.updateRefreshTokenAndLoginStatus(userDetails.getEmail(), jwtTokenDto.refreshToken(), true);
        setSuccessAppResponse(response, jwtTokenDto);

        // 테스트용
//        if (userAgent == null) {
//
//            return;
//        }
//        if (userAgent.contains("Android") || userAgent.contains("iPhone")) {
//            response.sendRedirect("poppin://login?email=" + userDetails.getUsername());
//        } else {
//            response.sendRedirect("http://localhost:3000/login?email=" + userDetails.getUsername());
//        }
    }

    private void setSuccessAppResponse(HttpServletResponse response, JwtTokenDto jwtTokenDto) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", Map.of("accessToken", jwtTokenDto.accessToken(), "refreshToken", jwtTokenDto.refreshToken()));
        result.put("error", null);

        response.getWriter().write(JSONValue.toJSONString(result));
    }
}
