package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.popup.domain.PreferedPopup;
import com.poppin.poppinserver.popup.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.popup.repository.PreferedPopupRepository;
import com.poppin.poppinserver.popup.usecase.PreferedPopupCommandUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreferedPopupCommandService implements PreferedPopupCommandUseCase {
    private final PreferedPopupRepository preferedPopupRepository;

    @Override
    public PreferedPopup createProxyPreferedPopup(PreferedPopup preferedPopup) {
        return preferedPopupRepository.save(
                PreferedPopup.builder()
                .wantFree(preferedPopup.getWantFree())
                .market(preferedPopup.getMarket())
                .experience(preferedPopup.getExperience())
                .display(preferedPopup.getDisplay())
                .build()
        );
    }

    @Override
    public PreferedPopup createPreferedPopup(PreferedPopup preferedPopup) {
        return preferedPopupRepository.save(preferedPopup);
    }

    @Override
    public PreferedPopup createPreferedPopup(CreatePreferedDto createPreferedDto) {
        return preferedPopupRepository.save(
                PreferedPopup.builder()
                        .market(createPreferedDto.market())
                        .display(createPreferedDto.display())
                        .experience(createPreferedDto.experience())
                        .wantFree(createPreferedDto.wantFree())
                        .build()
        );
    }

    @Override
    public PreferedPopup createPreferedPopup(List<String> prepered) {
        return preferedPopupRepository.save(
                PreferedPopup.builder()
                        .market(prepered.contains("market"))
                        .experience(prepered.contains("experience"))
                        .display(prepered.contains("display"))
                        .build()
        );
    }

    @Override
    public PreferedPopup createEmptyPreferedPopup() {
        return preferedPopupRepository.save(
                PreferedPopup.builder()
                        .wantFree(false)
                        .market(false)
                        .experience(false)
                        .display(false)
                        .build()
        );
    }

    @Override
    public void updatePreferedPopup(PreferedPopup preferedPopup, CreatePreferedDto createPreferedDto) {
        preferedPopup.update(createPreferedDto.market(),
                createPreferedDto.display(),
                createPreferedDto.experience(),
                createPreferedDto.wantFree());
        preferedPopupRepository.save(preferedPopup);
    }
}
