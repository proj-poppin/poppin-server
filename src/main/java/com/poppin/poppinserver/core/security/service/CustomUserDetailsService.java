package com.poppin.poppinserver.core.security.service;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.security.info.CustomUserDetails;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserQueryRepository userQueryRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userQueryRepository.findByEmail(email)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        return CustomUserDetails.create(user);
    }

    public UserDetails loadUserByUserId(Long userId) {
        User user = userQueryRepository.findByIdAndRefreshTokenNotNull(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        return CustomUserDetails.create(user);
    }
}
