package com.poppin.poppinserver.core.security.provider;

import com.poppin.poppinserver.core.security.info.CustomUserDetails;
import com.poppin.poppinserver.core.security.info.JwtAuthenticationToken;
import com.poppin.poppinserver.core.security.info.JwtUserInfo;
import com.poppin.poppinserver.core.security.service.CustomUserDetailsService;
import com.poppin.poppinserver.user.domain.type.EUserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            return null;
        }

        Long userId = (Long) jwtAuthenticationToken.getPrincipal();
        EUserRole role = (EUserRole) jwtAuthenticationToken.getCredentials();

        JwtUserInfo jwtUserInfo = JwtUserInfo.builder()
                .id(userId)
                .role(role)
                .build();

        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUserId(jwtUserInfo.id());
        if (userDetails == null) {
            throw new UsernameNotFoundException("User not found for ID: " + jwtUserInfo.id());
        }

        if (!userDetails.getRole().equals(jwtUserInfo.role())) {
            throw new AuthenticationException("Invalid Role") {
            };
        }

        // 인증 성공 시 UsernamePasswordAuthenticationToken에 사용자 정보와 권한 정보를 담아 반환
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

//        CustomUserDetails userDetails = null;
//        if (authentication instanceof UsernamePasswordAuthenticationToken) {
//            userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(
//                    (String) authentication.getPrincipal());
//            if (!bCryptPasswordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword())) {
//                throw new AuthenticationException("Invalid Password") {
//                };
//            }
//        } else if (authentication instanceof JwtAuthenticationToken) {
//            JwtUserInfo jwtUserInfo = JwtUserInfo.builder()
//                    .id((Long) authentication.getPrincipal())
//                    .role((EUserRole) authentication.getCredentials())
//                    .build();
//            userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUserId(jwtUserInfo.id());
//            if (userDetails.getRole() != jwtUserInfo.role()) {
//                throw new AuthenticationException("Invalid Role") {
//                };
//            }
//        } else {
//            throw new AuthenticationException("Invalid Authentication") {
//            };
//        }
//        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
//        return authentication.equals(JwtAuthenticationToken.class) || authentication.equals(
//                UsernamePasswordAuthenticationToken.class);
    }
}
