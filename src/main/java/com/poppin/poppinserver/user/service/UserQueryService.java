package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService implements UserQueryUseCase {
    private final UserQueryRepository userQueryRepository;

    @Override
    public User findUserById(Long userId) {
        return userQueryRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
    }
}
