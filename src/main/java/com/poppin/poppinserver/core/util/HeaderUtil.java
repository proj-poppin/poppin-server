package com.poppin.poppinserver.core.util;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class HeaderUtil {
    private final JwtUtil jwtUtil;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    public static String refineHeader(String header, String prefix) {
        if (!StringUtils.hasText(header) || !header.startsWith(prefix)) {
            throw new CommonException(ErrorCode.INVALID_HEADER);
        }
        return header.substring(prefix.length());
    }

    public Long parseUserId(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }

        // "Bearer " 이후의 토큰 부분 추출
        String token = refineHeader(authorizationHeader, BEARER_PREFIX);

        return jwtUtil.getUserIdFromToken(token);
    }
}
