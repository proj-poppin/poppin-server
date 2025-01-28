package com.poppin.poppinserver.core.security.filter;

import com.poppin.poppinserver.core.constant.Constant;
import com.poppin.poppinserver.core.security.JwtAuthenticationToken;
import com.poppin.poppinserver.core.security.info.JwtUserInfo;
import com.poppin.poppinserver.core.security.provider.JwtAuthenticationProvider;
import com.poppin.poppinserver.core.util.JwtUtil;
import com.poppin.poppinserver.user.domain.type.EUserRole;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Constant.NO_NEED_AUTH_URLS.stream().anyMatch(request.getRequestURI()::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String token = jwtUtil.getJwtFromRequest(request);
        if (StringUtils.hasText(token)) {
            Claims claims = jwtUtil.validateAndGetClaimsFromToken(token);
            JwtUserInfo jwtUserInfo = JwtUserInfo.builder()
                    .id(claims.get(Constant.USER_ID_CLAIM_NAME, Long.class))
                    .role(EUserRole.valueOf(claims.get(Constant.USER_ROLE_CLAIM_NAME, String.class)))
                    .build();

            JwtAuthenticationToken beforeAuthentication = new JwtAuthenticationToken(null, jwtUserInfo.id(),
                    jwtUserInfo.role());

            UsernamePasswordAuthenticationToken afterAuthentication = (UsernamePasswordAuthenticationToken) jwtAuthenticationProvider.authenticate(
                    beforeAuthentication);
            afterAuthentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(afterAuthentication);
            SecurityContextHolder.setContext(securityContext);
        }

        filterChain.doFilter(request, response);
    }
}
