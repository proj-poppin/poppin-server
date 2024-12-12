package com.poppin.poppinserver.visit.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.visit.domain.Visit;

import java.util.List;
import java.util.Optional;

@UseCase
public interface VisitQueryUseCase {
    Optional<Integer> getRealTimeVisitors(Long popupId);
    Optional<Visit> findByUserId(Long userId, Long popupId);

    List<Visit> findAllByUserId(Long userId);

}
