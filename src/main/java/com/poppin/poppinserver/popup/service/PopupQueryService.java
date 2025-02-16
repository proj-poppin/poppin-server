package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.dto.popup.response.PopupStoreDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupSummaryDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupTasteDto;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.popup.usecase.PopupQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopupQueryService implements PopupQueryUseCase {
    private final PopupRepository popupRepository;

    @Override
    public Popup findPopupById(Long popupId) {
        return popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));
    }

    @Override
    public Boolean existsPopupById(Long popupId) {
        return popupRepository.existsById(popupId);
    }

    @Override
    public List<Popup> findHotPopupList() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startOfDay = yesterday.atStartOfDay();
        LocalDateTime endOfDay = yesterday.plusDays(1).atStartOfDay();

        return popupRepository.findTopOperatingPopupsByInterestAndViewCount(
                startOfDay,
                endOfDay,
                PageRequest.of(0, 5)
        );
    }

    @Override
    public List<Popup> findHotPopupList(Long userId) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startOfDay = yesterday.atStartOfDay();
        LocalDateTime endOfDay = yesterday.plusDays(1).atStartOfDay();

        return popupRepository.findTopOperatingPopupsByInterestAndViewCount(
                startOfDay,
                endOfDay,
                userId,
                PageRequest.of(0, 5)
        );
    }

    @Override
    public List<Popup> findNewPopupList() {
        return popupRepository.findNewOpenPopupByAll(
                PageRequest.of(0, 5)
        );
    }

    @Override
    public List<Popup> findNewPopupList(Long userId) {
        return popupRepository.findNewOpenPopupByAll(
                userId,
                PageRequest.of(0, 5)
        );
    }

    @Override
    public List<Popup> findClosingPopupList() {
        return popupRepository.findClosingPopupByAll(
                PageRequest.of(0, 5)
        );
    }

    @Override
    public List<Popup> findClosingPopupList(Long userId) {
        return popupRepository.findClosingPopupByAll(
                userId,
                PageRequest.of(0, 5)
        );
    }

    @Override
    public List<Popup> findTastePopupList() {
        return List.of();
    }

    @Override
    public List<Popup> findTastePopupList(Long userId) {
        return List.of();
    }
}
