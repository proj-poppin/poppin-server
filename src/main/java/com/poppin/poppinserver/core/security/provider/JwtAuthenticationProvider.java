package com.poppin.poppinserver.core.security.provider;

import com.poppin.poppinserver.core.security.JwtAuthenticationToken;
import com.poppin.poppinserver.core.security.info.CustomUserDetails;
import com.poppin.poppinserver.core.security.info.JwtUserInfo;
import com.poppin.poppinserver.core.security.service.CustomUserDetailsService;
import com.poppin.poppinserver.user.domain.type.EUserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final CustomUserDetailsService customUserDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CustomUserDetails userDetails = null;
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(
                    (String) authentication.getPrincipal());
            if (!bCryptPasswordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword())) {
                throw new AuthenticationException("Invalid Password") {
                };
            }
        } else if (authentication instanceof JwtAuthenticationToken) {
            JwtUserInfo jwtUserInfo = JwtUserInfo.builder()
                    .id((Long) authentication.getPrincipal())
                    .role((EUserRole) authentication.getCredentials())
                    .build();
            userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUserId(jwtUserInfo.id());
            if (userDetails.getRole() != jwtUserInfo.role()) {
                throw new AuthenticationException("Invalid Role") {
                };
            }
        } else {
            throw new AuthenticationException("Invalid Authentication") {
            };
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(JwtAuthenticationToken.class) || authentication.equals(
                UsernamePasswordAuthenticationToken.class);
    }
}
