package com.poppin.poppinserver.interest.service;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.interest.domain.Interest;
import com.poppin.poppinserver.interest.repository.InterestQueryRepository;
import com.poppin.poppinserver.interest.usercase.InterestQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class InterestQueryService implements InterestQueryUseCase {
    private final InterestQueryRepository interestQueryRepository;

    @Override
    public Interest findInterestByUserIdAndPopupId(Long userId, Long popupId) {
        return interestQueryRepository.findByUserIdAndPopupId(userId, popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_INTEREST));
    }

    public Boolean existsInterestByUserIdAndPopupId(Long userId, Long popupId) {
        return interestQueryRepository.existsByUserIdAndPopupId(userId, popupId);
    }

    @Override
    public LocalDateTime findCreatedAtByUserIdAndPopupId(Long userId, Long popupId) {
        return interestQueryRepository.findCreatedAtByUserIdAndPopupId(userId, popupId);
    }

}
