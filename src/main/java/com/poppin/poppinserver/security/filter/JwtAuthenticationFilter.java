package com.poppin.poppinserver.security.filter;

import com.poppin.poppinserver.constant.Constant;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.security.JwtAuthenticationToken;
import com.poppin.poppinserver.security.info.JwtUserInfo;
import com.poppin.poppinserver.security.provider.JwtAuthenticationProvider;
import com.poppin.poppinserver.type.EUserRole;
import com.poppin.poppinserver.util.HeaderUtil;
import com.poppin.poppinserver.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Constant.NO_NEED_AUTH_URLS.stream().anyMatch(request.getRequestURI()::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = HeaderUtil.refineHeader(request, Constant.AUTHORIZATION_HEADER, Constant.BEARER_PREFIX)
                .orElseThrow(() -> new CommonException(ErrorCode.INVALID_TOKEN_ERROR));

        Claims claims = jwtUtil.validateAndGetClaimsFromToken(token);
        JwtUserInfo jwtUserInfo = JwtUserInfo.builder()
                .email(claims.get(Constant.USER_EMAIL_CLAIM_NAME, String.class))
                .role(EUserRole.valueOf(claims.get(Constant.USER_ROLE_CLAIM_NAME, String.class)))
                .build();

        JwtAuthenticationToken beforeAuthentication = new JwtAuthenticationToken(null, jwtUserInfo.email(), jwtUserInfo.role());

        UsernamePasswordAuthenticationToken afterAuthentication = (UsernamePasswordAuthenticationToken) jwtAuthenticationProvider.authenticate(beforeAuthentication);
        afterAuthentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(afterAuthentication);
        SecurityContextHolder.setContext(securityContext);

        filterChain.doFilter(request, response);
    }
}
