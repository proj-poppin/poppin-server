package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.managerInform.request.CreateManagerInformDto;
import com.poppin.poppinserver.dto.managerInform.response.ManagerInformDto;
import com.poppin.poppinserver.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.dto.userInform.response.UserInformDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.*;
import com.poppin.poppinserver.type.EInformProgress;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ManagerInformService {
    private final ManagerInformRepository managerInformRepository;
    private final PopupRepository popupRepository;
    private final TastePopupRepository tastePopupRepository;
    private final PosterImageRepository posterImageRepository;
    private final UserRepository userRepository;
    private final AlarmKeywordRepository alarmKeywordRepository;
    private final PreferedPopupRepository preferedPopupRepository;

    private final S3Service s3Service;

    @Transactional
    public ManagerInformDto createManagerInform(CreateManagerInformDto createManagerInformDto, //운영자 제보 생성
                                                List<MultipartFile> images,
                                                Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        CreateTasteDto createTasteDto = createManagerInformDto.taste();
        TastePopup tastePopup = TastePopup.builder()
                .fasionBeauty(createTasteDto.fasionBeauty())
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
                .alchol(createTasteDto.alchol())
                .animalPlant(createTasteDto.animalPlant())
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
                .entranceFee(createManagerInformDto.entranceFee())
                .resvRequired(createManagerInformDto.resvRequired())
                .introduce(createManagerInformDto.introduce())
                .address(createManagerInformDto.address())
                .addressDetail(createManagerInformDto.addressDetail())
                .openDate(createManagerInformDto.openDate())
                .openTime(createManagerInformDto.openTime())
                .operationExcept(createManagerInformDto.operationExcept())
                .operationStatus("EXECUTING")
                .parkingAvailable(createManagerInformDto.parkingAvailable())
                .preferedPopup(preferedPopup)
                .tastePopup(tastePopup)
                .build();
        popup = popupRepository.save(popup);
        log.info(popup.toString());

        // 팝업 이미지 처리 및 저장
        List<String> fileUrls = s3Service.uploadPopupPoster(images, popup.getId());

        List<PosterImage> posterImages = new ArrayList<>();
        for(String url : fileUrls){
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
                .informedAt(LocalDateTime.now())
                .popupId(popup)
                .informerEmail(createManagerInformDto.informerEmail())
                .affiliation(createManagerInformDto.affiliation())
                .progress(EInformProgress.NOTEXECUTED)
                .build();
        managerInform = managerInformRepository.save(managerInform);

        return ManagerInformDto.fromEntity(managerInform);
    }

    @Transactional
    public ManagerInformDto readManageInform(Long manageInformId){ // 운영자 제보 조회
        ManagerInform managerInform = managerInformRepository.findById(manageInformId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MANAGE_INFORM));

        return ManagerInformDto.fromEntity(managerInform);
    }
}
