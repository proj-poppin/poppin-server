package com.poppin.poppinserver.security.provider;

import com.poppin.poppinserver.security.JwtAuthenticationToken;
import com.poppin.poppinserver.security.info.CustomUserDetails;
import com.poppin.poppinserver.security.info.JwtUserInfo;
import com.poppin.poppinserver.security.service.CustomUserDetailsService;
import com.poppin.poppinserver.type.EUserRole;
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
            userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername((String) authentication.getPrincipal());
            if (!bCryptPasswordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword())) {
                throw new AuthenticationException("Invalid Password") {};
            }
        } else if (authentication instanceof JwtAuthenticationToken) {
            JwtUserInfo jwtUserInfo = JwtUserInfo.builder()
                    .email((String) authentication.getPrincipal())
                    .role((EUserRole) authentication.getCredentials())
                    .build();
            userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(jwtUserInfo.email());
            if (userDetails.getRole() != jwtUserInfo.role()) {
                throw new AuthenticationException("Invalid Role") {};
            }
        } else {
            throw new AuthenticationException("Invalid Authentication") {};
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(JwtAuthenticationToken.class) || authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
