package com.poppin.poppinserver.inform.service;

import com.poppin.poppinserver.alarm.repository.PopupAlarmKeywordRepository;
import com.poppin.poppinserver.core.type.EInformProgress;
import com.poppin.poppinserver.core.type.EOperationStatus;
import com.poppin.poppinserver.inform.domain.ManagerInform;
import com.poppin.poppinserver.inform.dto.managerInform.request.CreateManagerInformDto;
import com.poppin.poppinserver.inform.dto.managerInform.response.ManagerInformDto;
import com.poppin.poppinserver.inform.repository.ManagerInformRepository;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.popup.domain.PreferedPopup;
import com.poppin.poppinserver.popup.domain.TastePopup;
import com.poppin.poppinserver.popup.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.popup.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.popup.repository.PosterImageRepository;
import com.poppin.poppinserver.popup.repository.PreferedPopupRepository;
import com.poppin.poppinserver.popup.repository.TastePopupRepository;
import com.poppin.poppinserver.popup.service.S3Service;
import com.poppin.poppinserver.popup.usecase.PosterImageCommandUseCase;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class ManagerInformService {
    private final ManagerInformRepository managerInformRepository;
    private final PopupRepository popupRepository;
    private final TastePopupRepository tastePopupRepository;
    private final PreferedPopupRepository preferedPopupRepository;

    private final UserQueryUseCase userQueryUseCase;
    private final PosterImageCommandUseCase posterImageCommandUseCase;

    @Transactional
    public ManagerInformDto createManagerInform(CreateManagerInformDto createManagerInformDto,
                                                String filteringThreeCategories,
                                                String filteringFourteenCategories,
                                                List<MultipartFile> images,
                                                Long userId) {
        User user = userQueryUseCase.findUserById(userId);

        List<String> taste = Arrays.stream(filteringThreeCategories.split(",")).toList();
        List<String> prepered = Arrays.stream(filteringFourteenCategories.split(",")).toList();

        TastePopup tastePopup = TastePopup.builder()
                .fasionBeauty(prepered.contains("fashionBeauty"))
                .characters(prepered.contains("characters"))
                .foodBeverage(prepered.contains("foodBeverage"))
                .webtoonAni(prepered.contains("webtoonAni"))
                .interiorThings(prepered.contains("interiorThings"))
                .movie(prepered.contains("movie"))
                .musical(prepered.contains("musical"))
                .sports(prepered.contains("sports"))
                .game(prepered.contains("game"))
                .itTech(prepered.contains("itTech"))
                .kpop(prepered.contains("kpop"))
                .alcohol(prepered.contains("alcohol"))
                .animalPlant(prepered.contains("animalPlant"))
                .etc(prepered.contains("etc"))
                .build();
        tastePopupRepository.save(tastePopup);

        PreferedPopup preferedPopup = PreferedPopup.builder()
                .wantFree(taste.contains("wantFree"))
                .market(taste.contains("market"))
                .experience(taste.contains("experience"))
                .display(taste.contains("display"))
                .build();
        preferedPopupRepository.save(preferedPopup);

        Popup popup = Popup.builder()
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
                .build();
        popup = popupRepository.save(popup);
        log.info(popup.toString());

        // 팝업 이미지 처리 및 저장
        List<PosterImage> posterImages = posterImageCommandUseCase.savePosterList(images, popup);
        popup.updatePosterUrl(posterImages.get(0).getPosterUrl());

        popup = popupRepository.save(popup);

        ManagerInform managerInform = ManagerInform.builder()
                .informerId(user)
                .popupId(popup)
                .informerEmail(createManagerInformDto.informerEmail())
                .affiliation(createManagerInformDto.affiliation())
                .progress(EInformProgress.NOTEXECUTED)
                .build();
        managerInform = managerInformRepository.save(managerInform);

        return ManagerInformDto.fromEntity(managerInform);
    } //운영자 제보 생성

    @Transactional
    public ManagerInformDto createGuestManagerInform(CreateManagerInformDto createManagerInformDto,
                                                     String filteringThreeCategories,
                                                     String filteringFourteenCategories,
                                                     List<MultipartFile> images) {

        List<String> taste = Arrays.stream(filteringThreeCategories.split(",")).toList();
        List<String> prepered = Arrays.stream(filteringFourteenCategories.split(",")).toList();

        TastePopup tastePopup = TastePopup.builder()
                .fasionBeauty(prepered.contains("fashionBeauty"))
                .characters(prepered.contains("characters"))
                .foodBeverage(prepered.contains("foodBeverage"))
                .webtoonAni(prepered.contains("webtoonAni"))
                .interiorThings(prepered.contains("interiorThings"))
                .movie(prepered.contains("movie"))
                .musical(prepered.contains("musical"))
                .sports(prepered.contains("sports"))
                .game(prepered.contains("game"))
                .itTech(prepered.contains("itTech"))
                .kpop(prepered.contains("kpop"))
                .alcohol(prepered.contains("alcohol"))
                .animalPlant(prepered.contains("animalPlant"))
                .etc(prepered.contains("etc"))
                .build();
        tastePopupRepository.save(tastePopup);

        PreferedPopup preferedPopup = PreferedPopup.builder()
                .wantFree(taste.contains("wantFree"))
                .market(taste.contains("market"))
                .experience(taste.contains("experience"))
                .display(taste.contains("display"))
                .build();
        preferedPopupRepository.save(preferedPopup);

        Popup popup = Popup.builder()
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
                .build();
        popup = popupRepository.save(popup);
        log.info(popup.toString());

        // 팝업 이미지 처리 및 저장
        List<PosterImage> posterImages = posterImageCommandUseCase.savePosterList(images, popup);
        popup.updatePosterUrl(posterImages.get(0).getPosterUrl());

        popup = popupRepository.save(popup);

        ManagerInform managerInform = ManagerInform.builder()
                .informerId(null)
                .popupId(popup)
                .informerEmail(createManagerInformDto.informerEmail())
                .affiliation(createManagerInformDto.affiliation())
                .progress(EInformProgress.NOTEXECUTED)
                .build();
        managerInform = managerInformRepository.save(managerInform);

        return ManagerInformDto.fromEntity(managerInform);
    } //운영자 제보 생성
}
