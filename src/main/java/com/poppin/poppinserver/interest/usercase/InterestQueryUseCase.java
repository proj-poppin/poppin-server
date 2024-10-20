package com.poppin.poppinserver.interest.usercase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.interest.domain.Interest;

import java.util.Optional;

@UseCase
public interface InterestQueryUseCase {
    Interest findInterestByUserIdAndPopupId(Long userId, Long popupId);
    Boolean existsInterestByUserIdAndPopupId(Long userId, Long popupId);
}
