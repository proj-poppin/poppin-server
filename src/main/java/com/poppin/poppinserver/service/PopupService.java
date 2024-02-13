package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.dto.Popup.request.CreatePopupDto;
import com.poppin.poppinserver.dto.Popup.response.PopupDto;
import com.poppin.poppinserver.dto.Popup.response.PopupSummaryDto;
import com.poppin.poppinserver.repository.PopupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
                //.posterUrl(createPopupDto.posterUrl())
                .build();

        return PopupDto.fromEntity(popupRepository.save(popup));
    }

    public List<PopupSummaryDto> readHotList(){
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startOfDay = yesterday.atStartOfDay();
        LocalDateTime endOfDay = yesterday.plusDays(1).atStartOfDay();

        List<Popup> popups = popupRepository.findTopOperatingPopupsByInterestAndViewCount(startOfDay, endOfDay, PageRequest.of(0, 5));

        return PopupSummaryDto.fromEntityList(popups);
    }

    public List<PopupSummaryDto> readNewList(){

        List<Popup> popups = popupRepository.findNewOpenPopupByAll(PageRequest.of(0, 5));

        return PopupSummaryDto.fromEntityList(popups);
    }
}
