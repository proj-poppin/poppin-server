package com.poppin.poppinserver.interest.service;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.interest.domain.Interest;
import com.poppin.poppinserver.interest.repository.InterestRepository;
import com.poppin.poppinserver.interest.usercase.InterestQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InterestQueryService implements InterestQueryUseCase {
    private final InterestRepository interestRepository;

    @Override
    public Interest findInterestByUserIdAndPopupId(Long userId, Long popupId) {
        return interestRepository.findByUserIdAndPopupId(userId, popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_INTEREST));
    }

    public Boolean existsInterestByUserIdAndPopupId(Long userId, Long popupId) {
        return interestRepository.existsByUserIdAndPopupId(userId, popupId);
    }
}
