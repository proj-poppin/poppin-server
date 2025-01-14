package com.poppin.poppinserver.user.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.dto.auth.request.AuthSignUpRequestDto;

@UseCase
public interface UserCommandUseCase {
    User createUserByDefaultSignUp(AuthSignUpRequestDto authSignUpRequestDto);

    User createUserBySocialSignUp(AuthSignUpRequestDto authSignUpRequestDto);

}
