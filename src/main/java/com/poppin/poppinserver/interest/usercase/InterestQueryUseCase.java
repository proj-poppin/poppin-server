package com.poppin.poppinserver.interest.usercase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.interest.domain.Interest;

import java.time.LocalDateTime;
import java.util.Optional;

@UseCase
public interface InterestQueryUseCase {
    Interest findInterestByUserIdAndPopupId(Long userId, Long popupId);
    Boolean existsInterestByUserIdAndPopupId(Long userId, Long popupId);
    LocalDateTime findCreatedAtByUserIdAndPopupId(Long userId, Long popupId);
}
