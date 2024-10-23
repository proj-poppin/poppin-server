package com.poppin.poppinserver.user.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.user.domain.User;

@UseCase
public interface UserQueryUseCase {
    User findUserById(Long userId);
}
