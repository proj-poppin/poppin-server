package com.poppin.poppinserver.visit.service;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.usecase.PopupQueryUseCase;
import com.poppin.poppinserver.visit.domain.Visit;
import com.poppin.poppinserver.visit.repository.VisitRepository;
import com.poppin.poppinserver.visit.usecase.VisitQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class VisitQueryService implements VisitQueryUseCase {

    private final VisitRepository visitRepository;
    private final PopupQueryUseCase popupQueryUseCase;

    @Override
    public Optional<Integer> getRealTimeVisitors(Long popupId) {
        Popup popup = popupQueryUseCase.findPopupById(popupId);

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime thirtyMinutesAgo = now.minus(30, ChronoUnit.MINUTES);

        Optional<Integer> realTimeVisitorsCount = visitRepository.showRealTimeVisitors(popup, thirtyMinutesAgo);

        return realTimeVisitorsCount;
    }

    @Override
    public Optional<Visit> findByUserId(Long userId, Long popupId) {
        return visitRepository.findByUserId(userId,popupId);
    }

    @Override
    public List<Visit> findAllByUserId(Long userId) {
        return visitRepository.findAllByUserId(userId);
    }
}
