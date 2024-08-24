package com.poppin.poppinserver.core.security.service;

import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.user.repository.UserRepository;
import com.poppin.poppinserver.core.security.info.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        return CustomUserDetails.create(user);
    }

    public UserDetails loadUserByUserId(Long userId) {
        User user = userRepository.findByIdAndIsLoginAndRefreshTokenNotNull(userId, true)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        return CustomUserDetails.create(user);
    }
}
