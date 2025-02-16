package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.popup.domain.TastePopup;
import com.poppin.poppinserver.popup.domain.WhoWithPopup;
import com.poppin.poppinserver.popup.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.popup.dto.popup.request.CreateWhoWithDto;
import com.poppin.poppinserver.popup.repository.TastePopupRepository;
import com.poppin.poppinserver.popup.usecase.TastedPopupCommandUseCase;
import com.poppin.poppinserver.popup.usecase.WhoWithPopupCommandUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhoWithPopupCommandService implements WhoWithPopupCommandUseCase {

    @Override
    public WhoWithPopup createWhoWithPopup(WhoWithPopup whoWithPopup) {
        return null;
    }

    @Override
    public WhoWithPopup createWhoWithPopup(CreateWhoWithDto createWhoWithDto) {
        return null;
    }
}
