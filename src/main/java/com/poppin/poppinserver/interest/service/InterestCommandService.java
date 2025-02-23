package com.poppin.poppinserver.interest.service;

import com.poppin.poppinserver.interest.domain.Interest;
import com.poppin.poppinserver.interest.repository.InterestCommandRepository;
import com.poppin.poppinserver.interest.repository.InterestQueryRepository;
import com.poppin.poppinserver.interest.usercase.InterestCommandUseCase;
import com.poppin.poppinserver.interest.usercase.InterestQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InterestCommandService implements InterestCommandUseCase {
    private final InterestCommandRepository interestCommandRepository;

    private final InterestQueryUseCase interestQueryUseCase;

    @Override
    public void deleteAllInterestsByPopupId(Long popupId) {
        interestCommandRepository.deleteAllByPopupId(popupId);
    }

    @Override
    public void deleteExistByUserIdAndPopupId(Long userId, Long popupId) {
        Interest interest = interestQueryUseCase.findInterestByUserIdAndPopupId(userId, popupId);

        interestCommandRepository.delete(interest);
    }
}
