package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.managerInform.request.CreateManagerInformDto;
import com.poppin.poppinserver.dto.managerInform.request.UpdateManagerInfromDto;
import com.poppin.poppinserver.dto.managerInform.response.ManagerInformDto;
import com.poppin.poppinserver.dto.managerInform.response.ManagerInformSummaryDto;
import com.poppin.poppinserver.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.dto.userInform.response.UserInformSummaryDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.*;
import com.poppin.poppinserver.type.EInformProgress;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
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
                .alchol(createTasteDto.alcohol())
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

    @Transactional
    public ManagerInformDto updateManageInform(UpdateManagerInfromDto updateManagerInfromDto,
                                               List<MultipartFile> images,
                                               Long adminId){
        ManagerInform managerInform = managerInformRepository.findById(updateManagerInfromDto.managerInformId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MANAGE_INFORM));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        CreateTasteDto createTasteDto = updateManagerInfromDto.taste();
        TastePopup tastePopup = managerInform.getPopupId().getTastePopup();
        tastePopup.update(createTasteDto.fashionBeauty(),
                createTasteDto.characters(),
                createTasteDto.foodBeverage(),
                createTasteDto.webtoonAni(),
                createTasteDto.interiorThings(),
                createTasteDto.movie(),
                createTasteDto.musical(),
                createTasteDto.sports(),
                createTasteDto.game(),
                createTasteDto.itTech(),
                createTasteDto.kpop(),
                createTasteDto.alcohol(),
                createTasteDto.animalPlant());
        tastePopupRepository.save(tastePopup);

        CreatePreferedDto createPreferedDto = updateManagerInfromDto.prefered();
        PreferedPopup preferedPopup = managerInform.getPopupId().getPreferedPopup();
        preferedPopup.update(createPreferedDto.market(),
                createPreferedDto.display(),
                createPreferedDto.experience(),
                createPreferedDto.wantFree());
        preferedPopupRepository.save(preferedPopup);

        Popup popup = managerInform.getPopupId();

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

        // 기존 키워드 삭제 및 다시 저장
        alarmKeywordRepository.deleteAll(popup.getAlarmKeywords());

        List<AlarmKeyword> alarmKeywords = new ArrayList<>();
        for(String keyword : updateManagerInfromDto.keywords()){
            alarmKeywords.add(AlarmKeyword.builder()
                    .popupId(popup)
                    .keyword(keyword)
                    .build());
        }
        alarmKeywordRepository.saveAll(alarmKeywords);

        popup.update(
                updateManagerInfromDto.homepageLink(),
                updateManagerInfromDto.name(),
                updateManagerInfromDto.introduce(),
                updateManagerInfromDto.address(),
                updateManagerInfromDto.addressDetail(),
                updateManagerInfromDto.entranceFee(),
                updateManagerInfromDto.resvRequired(),
                updateManagerInfromDto.availableAge(),
                updateManagerInfromDto.parkingAvailable(),
                updateManagerInfromDto.openDate(),
                updateManagerInfromDto.closeDate(),
                updateManagerInfromDto.openTime(),
                updateManagerInfromDto.closeTime(),
                updateManagerInfromDto.operationExcept(),
                "EXECUTING"
        );

        popup = popupRepository.save(popup);

        managerInform.update(EInformProgress.EXECUTING, admin);
        managerInform = managerInformRepository.save(managerInform);

        return ManagerInformDto.fromEntity(managerInform);
    }

    @Transactional
    public ManagerInformDto uploadPopup(UpdateManagerInfromDto updateManagerInfromDto,
                                               List<MultipartFile> images,
                                               Long adminId){
        ManagerInform managerInform = managerInformRepository.findById(updateManagerInfromDto.managerInformId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MANAGE_INFORM));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        CreateTasteDto createTasteDto = updateManagerInfromDto.taste();
        TastePopup tastePopup = managerInform.getPopupId().getTastePopup();
        tastePopup.update(createTasteDto.fashionBeauty(),
                createTasteDto.characters(),
                createTasteDto.foodBeverage(),
                createTasteDto.webtoonAni(),
                createTasteDto.interiorThings(),
                createTasteDto.movie(),
                createTasteDto.musical(),
                createTasteDto.sports(),
                createTasteDto.game(),
                createTasteDto.itTech(),
                createTasteDto.kpop(),
                createTasteDto.alcohol(),
                createTasteDto.animalPlant());
        tastePopupRepository.save(tastePopup);

        CreatePreferedDto createPreferedDto = updateManagerInfromDto.prefered();
        PreferedPopup preferedPopup = managerInform.getPopupId().getPreferedPopup();
        preferedPopup.update(createPreferedDto.market(),
                createPreferedDto.display(),
                createPreferedDto.experience(),
                createPreferedDto.wantFree());
        preferedPopupRepository.save(preferedPopup);

        Popup popup = managerInform.getPopupId();

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

        // 기존 키워드 삭제 및 다시 저장
        alarmKeywordRepository.deleteAll(popup.getAlarmKeywords());

        List<AlarmKeyword> alarmKeywords = new ArrayList<>();
        for(String keyword : updateManagerInfromDto.keywords()){
            alarmKeywords.add(AlarmKeyword.builder()
                    .popupId(popup)
                    .keyword(keyword)
                    .build());
        }
        alarmKeywordRepository.saveAll(alarmKeywords);

        //날짜 요청 유효성 검증
        if (updateManagerInfromDto.openDate().isAfter(updateManagerInfromDto.closeDate())) {
            throw new CommonException(ErrorCode.INVALID_DATE_PARAMETER);
        }

        //현재 운영상태 정의
        String operationStatus;
        if (updateManagerInfromDto.openDate().isAfter(LocalDate.now())){
            Period period = Period.between(LocalDate.now(), updateManagerInfromDto.openDate());
            operationStatus = "D-" + period.getDays();
        } else if (updateManagerInfromDto.closeDate().isBefore(LocalDate.now())) {
            operationStatus = "TERMINATED";
        }
        else{
            operationStatus = "OPERATING";
        }

        popup.update(
                updateManagerInfromDto.homepageLink(),
                updateManagerInfromDto.name(),
                updateManagerInfromDto.introduce(),
                updateManagerInfromDto.address(),
                updateManagerInfromDto.addressDetail(),
                updateManagerInfromDto.entranceFee(),
                updateManagerInfromDto.resvRequired(),
                updateManagerInfromDto.availableAge(),
                updateManagerInfromDto.parkingAvailable(),
                updateManagerInfromDto.openDate(),
                updateManagerInfromDto.closeDate(),
                updateManagerInfromDto.openTime(),
                updateManagerInfromDto.closeTime(),
                updateManagerInfromDto.operationExcept(),
                operationStatus
        );

        popup = popupRepository.save(popup);

        managerInform.update(EInformProgress.EXECUTING, admin);
        managerInform = managerInformRepository.save(managerInform);

        return ManagerInformDto.fromEntity(managerInform);
    }

    public List<ManagerInformSummaryDto> reatManagerInformList(){
        List<ManagerInform> managerInforms = managerInformRepository.findAll();

        return ManagerInformSummaryDto.fromEntityList(managerInforms);
    }
}
