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
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import java.util.ArrayList;
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
    private final PosterImageRepository posterImageRepository;
    private final UserQueryUseCase userQueryUseCase;
    private final PopupAlarmKeywordRepository popupAlarmKeywordRepository;
    private final PreferedPopupRepository preferedPopupRepository;

    private final S3Service s3Service;

    @Transactional
    public ManagerInformDto createManagerInform(CreateManagerInformDto createManagerInformDto,
                                                List<MultipartFile> images,
                                                Long userId) {
        User user = userQueryUseCase.findUserById(userId);

        CreateTasteDto createTasteDto = createManagerInformDto.taste();
        TastePopup tastePopup = TastePopup.builder()
                .fasionBeauty(createTasteDto.fashionBeauty())
                .characters(createTasteDto.characters())
                .foodBeverage(createTasteDto.foodBeverage())
                .webtoonAni(createTasteDto.webtoonAni())
                .interiorThings(createTasteDto.interiorThings())
                .movie(createTasteDto.movie())
                .musical(createTasteDto.musical())
                .sports(createTasteDto.sports())
                .game(createTasteDto.game())
                .itTech(createTasteDto.itTech())
                .kpop(createTasteDto.kpop())
                .alcohol(createTasteDto.alcohol())
                .animalPlant(createTasteDto.animalPlant())
                .etc(createTasteDto.etc())
                .build();
        tastePopupRepository.save(tastePopup);

        CreatePreferedDto createPreferedDto = createManagerInformDto.prefered();
        PreferedPopup preferedPopup = PreferedPopup.builder()
                .wantFree(createPreferedDto.wantFree())
                .market(createPreferedDto.market())
                .experience(createPreferedDto.experience())
                .display(createPreferedDto.display())
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
                .preferedPopup(preferedPopup)
                .tastePopup(tastePopup)
                .build();
        popup = popupRepository.save(popup);
        log.info(popup.toString());

        // 팝업 이미지 처리 및 저장
        List<String> fileUrls = s3Service.uploadPopupPoster(images, popup.getId());

        List<PosterImage> posterImages = new ArrayList<>();
        for (String url : fileUrls) {
            PosterImage posterImage = PosterImage.builder()
                    .posterUrl(url)
                    .popup(popup)
                    .build();
            posterImages.add(posterImage);
        }
        posterImageRepository.saveAll(posterImages);
        popup.updatePosterUrl(fileUrls.get(0));

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
                                                     List<MultipartFile> images) {
        CreateTasteDto createTasteDto = createManagerInformDto.taste();
        TastePopup tastePopup = TastePopup.builder()
                .fasionBeauty(createTasteDto.fashionBeauty())
                .characters(createTasteDto.characters())
                .foodBeverage(createTasteDto.foodBeverage())
                .webtoonAni(createTasteDto.webtoonAni())
                .interiorThings(createTasteDto.interiorThings())
                .movie(createTasteDto.movie())
                .musical(createTasteDto.musical())
                .sports(createTasteDto.sports())
                .game(createTasteDto.game())
                .itTech(createTasteDto.itTech())
                .kpop(createTasteDto.kpop())
                .alcohol(createTasteDto.alcohol())
                .animalPlant(createTasteDto.animalPlant())
                .etc(createTasteDto.etc())
                .build();
        tastePopupRepository.save(tastePopup);

        CreatePreferedDto createPreferedDto = createManagerInformDto.prefered();
        PreferedPopup preferedPopup = PreferedPopup.builder()
                .wantFree(createPreferedDto.wantFree())
                .market(createPreferedDto.market())
                .experience(createPreferedDto.experience())
                .display(createPreferedDto.display())
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
                .preferedPopup(preferedPopup)
                .tastePopup(tastePopup)
                .build();
        popup = popupRepository.save(popup);
        log.info(popup.toString());

        // 팝업 이미지 처리 및 저장
        List<String> fileUrls = s3Service.uploadPopupPoster(images, popup.getId());

        List<PosterImage> posterImages = new ArrayList<>();
        for (String url : fileUrls) {
            PosterImage posterImage = PosterImage.builder()
                    .posterUrl(url)
                    .popup(popup)
                    .build();
            posterImages.add(posterImage);
        }
        posterImageRepository.saveAll(posterImages);
        popup.updatePosterUrl(fileUrls.get(0));

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
