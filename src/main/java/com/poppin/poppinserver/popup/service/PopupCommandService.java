package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.core.type.EOperationStatus;
import com.poppin.poppinserver.inform.dto.managerInform.request.CreateManagerInformDto;
import com.poppin.poppinserver.inform.dto.managerInform.request.UpdateManagerInformDto;
import com.poppin.poppinserver.inform.dto.userInform.request.CreateUserInformDto;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.popup.domain.PreferedPopup;
import com.poppin.poppinserver.popup.domain.TastePopup;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.popup.usecase.PopupCommandUseCase;
import com.poppin.poppinserver.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopupCommandService implements PopupCommandUseCase {
    private final PopupRepository popupRepository;

    @Override
    public Popup createPopup(CreateManagerInformDto createManagerInformDto, String operationStatus, TastePopup tastePopup, PreferedPopup preferedPopup) {
        return popupRepository.save(
                Popup.builder()
                        .homepageLink(createManagerInformDto.homepageLink())
                        .name(createManagerInformDto.name())
                        .availableAge(createManagerInformDto.availableAge())
                        .closeDate(createManagerInformDto.closeDate())
                        .closeTime(createManagerInformDto.closeTime())
                        .entranceRequired(createManagerInformDto.entranceRequired())
                        .entranceFee(createManagerInformDto.entranceFee())
                        .resvRequired(createManagerInformDto.resvRequired())
                        .introduce(createManagerInformDto.introduce())
                        .address(createManagerInformDto.address())
                        .addressDetail(createManagerInformDto.addressDetail())
                        .openDate(createManagerInformDto.openDate())
                        .openTime(createManagerInformDto.openTime())
                        .operationExcept(createManagerInformDto.operationExcept())
                        .operationStatus(EOperationStatus.EXECUTING.getStatus())
                        .parkingAvailable(createManagerInformDto.parkingAvailable())
                        .latitude(createManagerInformDto.latitude())
                        .longitude(createManagerInformDto.longitude())
                        .preferedPopup(preferedPopup)
                        .tastePopup(tastePopup)
                        .build()
        );
    }

    @Override
    public Popup createPopup(CreateUserInformDto createUserInformDto, String operationStatus, TastePopup tastePopup, PreferedPopup preferedPopup) {
        return popupRepository.save(
                Popup.builder()
                        .name(createUserInformDto.name())
                        .tastePopup(tastePopup)
                        .preferedPopup(preferedPopup)
                        .entranceRequired(true)
                        .operationStatus(EOperationStatus.EXECUTING.getStatus())
                        .build()
        );
    }

    @Override
    public void updatePopup(Popup popup, UpdateManagerInformDto updateManagerInformDto, String operationStatus, User agent) {
        popup.update(
                updateManagerInformDto.homepageLink(),
                updateManagerInformDto.name(),
                updateManagerInformDto.introduce(),
                updateManagerInformDto.address(),
                updateManagerInformDto.addressDetail(),
                updateManagerInformDto.entranceRequired(),
                updateManagerInformDto.entranceFee(),
                updateManagerInformDto.resvRequired(),
                updateManagerInformDto.availableAge(),
                updateManagerInformDto.parkingAvailable(),
                updateManagerInformDto.openDate(),
                updateManagerInformDto.closeDate(),
                updateManagerInformDto.openTime(),
                updateManagerInformDto.closeTime(),
                updateManagerInformDto.latitude(),
                updateManagerInformDto.longitude(),
                updateManagerInformDto.operationExcept(),
                operationStatus,
                agent
        );

        popupRepository.save(popup);
    }

    @Override
    public void updatePopupPosterUrl(Popup popup, PosterImage posterImage) {
        popup.updatePosterUrl(posterImage.getPosterUrl());

        popupRepository.save(popup);
    }
}
