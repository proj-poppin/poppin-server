package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.popup.usecase.PopupQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
