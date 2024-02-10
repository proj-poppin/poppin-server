package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.dto.Popup.request.CreatePopupDto;
import com.poppin.poppinserver.dto.Popup.response.PopupDto;
import com.poppin.poppinserver.repository.PopupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PopupService {
    private final PopupRepository popupRepository;

    public PopupDto createPopup(CreatePopupDto createPopupDto) {
        Popup popup = Popup.builder()
                .name(createPopupDto.name())
                .availableAge(createPopupDto.availableAge())
                .category(createPopupDto.category())
                .closeDate(createPopupDto.closeDate())
                .closeTime(createPopupDto.closeTime())
                .entranceFee(createPopupDto.entranceFee())
                .introduce(createPopupDto.introduce())
                .location(createPopupDto.location())
                .openDate(createPopupDto.openDate())
                .openTime(createPopupDto.openTime())
                .operationStatus(createPopupDto.operationStatus())
                .parkingAvailable(createPopupDto.parkingAvailable())
                .posterUrl(createPopupDto.posterUrl())
                .build();

        return PopupDto.fromEntity(popupRepository.save(popup));
    }
}
