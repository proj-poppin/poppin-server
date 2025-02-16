package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.popup.domain.WhoWithPopup;
import com.poppin.poppinserver.popup.dto.popup.request.CreateWhoWithDto;
import com.poppin.poppinserver.popup.repository.WhoWithPopupRepository;
import com.poppin.poppinserver.popup.usecase.WhoWithPopupCommandUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhoWithPopupCommandService implements WhoWithPopupCommandUseCase {
    private final WhoWithPopupRepository whoWithPopupRepository;
    @Override
    public WhoWithPopup createWhoWithPopup(WhoWithPopup whoWithPopup) {
        return whoWithPopupRepository.save(whoWithPopup);
    }

    @Override
    public WhoWithPopup updateWhoWithPopup(WhoWithPopup whoWithPopup, CreateWhoWithDto createWhoWithDto) {
        whoWithPopup.update(
                createWhoWithDto.solo(),
                createWhoWithDto.withFriend(),
                createWhoWithDto.withFamily(),
                createWhoWithDto.withLover()
        );
        return whoWithPopupRepository.save(whoWithPopup);
    }
}
