package com.poppin.poppinserver.core.security.handler;

import com.poppin.poppinserver.core.security.info.CustomUserDetails;
import com.poppin.poppinserver.core.util.JwtUtil;
import com.poppin.poppinserver.user.dto.auth.response.JwtTokenDto;
import com.poppin.poppinserver.user.repository.UserCommandRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONValue;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DefaultSignInSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final UserCommandRepository userCommandRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        JwtTokenDto jwtTokenDto = jwtUtil.generateToken(userDetails.getId(), userDetails.getRole());

        userCommandRepository.updateRefreshToken(userDetails.getId(), jwtTokenDto.refreshToken());
        setSuccessAppResponse(response, jwtTokenDto);
    }

    private void setSuccessAppResponse(HttpServletResponse response, JwtTokenDto jwtTokenDto) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data",
                Map.of("accessToken", jwtTokenDto.accessToken(), "refreshToken", jwtTokenDto.refreshToken()));
        result.put("error", null);

        response.getWriter().write(JSONValue.toJSONString(result));
    }
}
