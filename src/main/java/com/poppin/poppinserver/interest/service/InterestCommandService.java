package com.poppin.poppinserver.interest.service;

import com.poppin.poppinserver.interest.domain.Interest;
import com.poppin.poppinserver.interest.repository.InterestRepository;
import com.poppin.poppinserver.interest.usercase.InterestCommandUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InterestCommandService implements InterestCommandUseCase {
    private final InterestRepository interestRepository;

    @Override
    public void deleteAllInterestsByPopupId(Long popupId) {
        interestRepository.deleteAllByPopupId(popupId);
    }

    @Override
    public void deleteExistByUserIdAndPopupId(Long userId, Long popupId) {
        Optional<Interest> interest = interestRepository.findByUserIdAndPopupId(userId, popupId);

        interest.ifPresent(interestRepository::delete);
    }
}
