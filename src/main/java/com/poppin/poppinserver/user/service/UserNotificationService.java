package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.dto.user.response.UserNotificationResponseDto;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserNotificationService {
    private final UserQueryUseCase userQueryUseCase;
    private final UserActivityService userActivityService;

    @Transactional(readOnly = true)
    public UserNotificationResponseDto getNotifications(Long userId) {
        User user = userQueryUseCase.findUserById(userId);
        return userActivityService.getUserNotificationActivity(user);
    }
}
