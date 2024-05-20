package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.modifyInfo.request.CreateModifyInfoDto;
import com.poppin.poppinserver.dto.modifyInfo.request.UpdateModifyInfoDto;
import com.poppin.poppinserver.dto.modifyInfo.response.ModifyInfoDto;
import com.poppin.poppinserver.dto.modifyInfo.response.ModifyInfoSummaryDto;
import com.poppin.poppinserver.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.*;
import com.poppin.poppinserver.type.EInformProgress;
import com.poppin.poppinserver.type.EOperationStatus;
import com.poppin.poppinserver.util.ImageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModifyInfoService {
    private final ModifyInformRepository modifyInformRepository;
    private final ModifyImageReposiroty modifyImageReposiroty;
    private final UserRepository userRepository;
    private final PopupRepository popupRepository;
    private final PreferedPopupRepository preferedPopupRepository;
    private final TastePopupRepository tastePopupRepository;
    private final PosterImageRepository posterImageRepository;
    private final AlarmKeywordRepository alarmKeywordRepository;

    private final S3Service s3Service;

    @Transactional
    public ModifyInfoDto createModifyInfo(CreateModifyInfoDto createModifyInfoDto,
                                          List<MultipartFile> images,
                                          Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(createModifyInfoDto.popupId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        // 프록시 팝업 생성
        PreferedPopup preferedPopup = popup.getPreferedPopup();
        PreferedPopup proxyPrefered = PreferedPopup.builder()
                .wantFree(preferedPopup.getWantFree())
                .market(preferedPopup.getMarket())
                .experience(preferedPopup.getExperience())
                .display(preferedPopup.getDisplay())
                .build();
        proxyPrefered = preferedPopupRepository.save(proxyPrefered);

        TastePopup tastePopup = popup.getTastePopup();
        TastePopup proxyTaste = TastePopup.builder()
                .fasionBeauty(tastePopup.getFashionBeauty())
                .characters(tastePopup.getCharacters())
                .foodBeverage(tastePopup.getFoodBeverage())
                .webtoonAni(tastePopup.getWebtoonAni())
                .interiorThings(tastePopup.getInteriorThings())
                .movie(tastePopup.getMovie())
                .musical(tastePopup.getMusical())
                .sports(tastePopup.getSports())
                .game(tastePopup.getGame())
                .itTech(tastePopup.getItTech())
                .kpop(tastePopup.getKpop())
                .alchol(tastePopup.getAlcohol())
                .animalPlant(tastePopup.getAnimalPlant())
                .etc(tastePopup.getEtc())
                .build();
        proxyTaste = tastePopupRepository.save(proxyTaste);

        Popup proxyPopup = Popup.builder()
                .homepageLink(popup.getHomepageLink())
                .name(popup.getName())
                .availableAge(popup.getAvailableAge())
                .closeDate(popup.getCloseDate())
                .closeTime(popup.getCloseTime())
                .entranceFee(popup.getEntranceFee())
                .resvRequired(popup.getResvRequired())
                .introduce(popup.getIntroduce())
                .address(popup.getAddress())
                .addressDetail(popup.getAddressDetail())
                .openDate(popup.getOpenDate())
                .openTime(popup.getOpenTime())
                .operationExcept(popup.getOperationExcept())
                .operationStatus(EOperationStatus.EXECUTING)
                .parkingAvailable(popup.getParkingAvailable())
                .preferedPopup(proxyPrefered)
                .tastePopup(proxyTaste)
                .build();
        proxyPopup = popupRepository.save(proxyPopup);

        // 프록시 이미지와 생성
        List<PosterImage> posterImages = posterImageRepository.findByPopupId(popup);
        List<String> posterUrls = posterImages.stream()
                .map(PosterImage::getPosterUrl)
                .toList();

        List<String> proxyUrls = s3Service.copyImageListToAnotherFolder(posterUrls, proxyPopup.getId());

        List<PosterImage> proxyImages = new ArrayList<>();
        for(String proxyUrl : proxyUrls){
            PosterImage proxyImage = PosterImage.builder()
                    .posterUrl(proxyUrl)
                    .popup(proxyPopup)
                    .build();
            proxyImages.add(proxyImage);
        }
        posterImageRepository.saveAll(proxyImages);
        proxyPopup.updatePosterUrl(proxyImages.get(0).getPosterUrl());


        // 프록시 알람키워드 생성
        List<AlarmKeyword> alarmKeywords = alarmKeywordRepository.findByPopupId(popup);

        List<AlarmKeyword> proxyKeywords = new ArrayList<>();
        for (AlarmKeyword alarmKeyword : alarmKeywords) {
            AlarmKeyword proxyKeyword = AlarmKeyword.builder()
                    .popupId(proxyPopup)
                    .keyword(alarmKeyword.getKeyword())
                    .build();
        }
        alarmKeywordRepository.saveAll(proxyKeywords);

        // 정보수정요청 객체 저장
        ModifyInfo modifyInfo = ModifyInfo.builder()
                .content(createModifyInfoDto.content())
                .userId(user)
                .proxyPopup(proxyPopup)
                .originPopup(popup)
                .build();
        modifyInformRepository.save(modifyInfo);

        // 정보수정요청 이미지 처리 및 저장
        List<String> fileUrls = s3Service.uploadModifyInfo(images, modifyInfo.getId());

        List<ModifyImages> modifyImagesList = new ArrayList<>();
        for(String url : fileUrls){
            ModifyImages modifyImage = ModifyImages.builder()
                    .modifyId(modifyInfo)
                    .imageUrl(url)
                    .build();
            modifyImagesList.add(modifyImage);
        }
        modifyImageReposiroty.saveAll(modifyImagesList);

        return ModifyInfoDto.fromEntity(modifyInfo, fileUrls);
    } // 요청 생성

    @Transactional
    public ModifyInfoDto readModifyInfo(Long modifyInfoId){
        ModifyInfo modifyInfo = modifyInformRepository.findById(modifyInfoId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MODIFY_INFO));

        List<ModifyImages> modifyImageList = modifyImageReposiroty.findByModifyId(modifyInfo);

        List<String> imageList = new ArrayList<>();
        for(ModifyImages modifyImages : modifyImageList){
            imageList.add(modifyImages.getImageUrl());
        }

        return ModifyInfoDto.fromEntity(modifyInfo, imageList);
    } // 조회

    @Transactional
    public List<ModifyInfoSummaryDto> readModifyInfoList(){
        List<ModifyInfo> modifyInfoList = modifyInformRepository.findAll();

        return ModifyInfoSummaryDto.fromEntityList(modifyInfoList);
    }// 목록 조회

    @Transactional
    public ModifyInfoDto updateModifyInfo(UpdateModifyInfoDto updateModifyInfoDto,
                                               List<MultipartFile> images,
                                                Long adminId){
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.ACCESS_DENIED_ERROR));

        ModifyInfo modifyInfo = modifyInformRepository.findById(updateModifyInfoDto.modifyInfoId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MODIFY_INFO));

        CreateTasteDto createTasteDto = updateModifyInfoDto.taste();
        TastePopup tastePopup = modifyInfo.getProxyPopup().getTastePopup();
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
                createTasteDto.animalPlant(),
                createTasteDto.etc());
        tastePopupRepository.save(tastePopup);

        CreatePreferedDto createPreferedDto = updateModifyInfoDto.prefered();
        PreferedPopup preferedPopup = modifyInfo.getProxyPopup().getPreferedPopup();
        preferedPopup.update(createPreferedDto.market(),
                createPreferedDto.display(),
                createPreferedDto.experience(),
                createPreferedDto.wantFree());
        preferedPopupRepository.save(preferedPopup);

        Popup popup = modifyInfo.getProxyPopup();

        // 팝업 이미지 처리 및 저장

        // 기존 이미지 싹 지우기
        List<PosterImage> originImages = posterImageRepository.findByPopupId(popup);
        List<String> originUrls = originImages.stream()
                .map(PosterImage::getPosterUrl)
                .collect(Collectors.toList());
        s3Service.deleteMultipleImages(originUrls);
        posterImageRepository.deleteAllByPopupId(popup);

        //새로운 이미지 추가
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
        for(String keyword : updateModifyInfoDto.keywords()){
            alarmKeywords.add(AlarmKeyword.builder()
                    .popupId(popup)
                    .keyword(keyword)
                    .build());
        }
        alarmKeywordRepository.saveAll(alarmKeywords);

        popup.update(
                updateModifyInfoDto.homepageLink(),
                updateModifyInfoDto.name(),
                updateModifyInfoDto.introduce(),
                updateModifyInfoDto.address(),
                updateModifyInfoDto.addressDetail(),
                updateModifyInfoDto.entranceFee(),
                updateModifyInfoDto.resvRequired(),
                updateModifyInfoDto.availableAge(),
                updateModifyInfoDto.parkingAvailable(),
                updateModifyInfoDto.openDate(),
                updateModifyInfoDto.closeDate(),
                updateModifyInfoDto.openTime(),
                updateModifyInfoDto.closeTime(),
                updateModifyInfoDto.operationExcept(),
                EOperationStatus.EXECUTING,
                admin

        );

        modifyInfo = modifyInformRepository.save(modifyInfo);

        return ModifyInfoDto.fromEntity(modifyInfo, null);
    } // 임시 저장

    @Transactional
    public ModifyInfoDto uploadModifyInfo(UpdateModifyInfoDto updateModifyInfoDto,
                                        List<MultipartFile> images,
                                          Long adminId){
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.ACCESS_DENIED_ERROR));

        ModifyInfo modifyInfo = modifyInformRepository.findById(updateModifyInfoDto.modifyInfoId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MODIFY_INFO));

        // 기존 팝업에 수정 사항 덮어 씌우기
        CreateTasteDto createTasteDto = updateModifyInfoDto.taste();
        TastePopup tastePopup = modifyInfo.getOriginPopup().getTastePopup();
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
                createTasteDto.animalPlant(),
                createTasteDto.etc());
        tastePopupRepository.save(tastePopup);

        CreatePreferedDto createPreferedDto = updateModifyInfoDto.prefered();
        PreferedPopup preferedPopup = modifyInfo.getOriginPopup().getPreferedPopup();
        preferedPopup.update(createPreferedDto.market(),
                createPreferedDto.display(),
                createPreferedDto.experience(),
                createPreferedDto.wantFree());
        preferedPopupRepository.save(preferedPopup);

        // 팝업 이미지 처리 및 저장

        // 기존 이미지 싹 지우기
        Popup originPopup = modifyInfo.getOriginPopup();
        List<PosterImage> originImages = posterImageRepository.findByPopupId(originPopup);
        List<String> originUrls = originImages.stream()
                .map(PosterImage::getPosterUrl)
                .collect(Collectors.toList());
        s3Service.deleteMultipleImages(originUrls);
        posterImageRepository.deleteAllByPopupId(originPopup);

        // 프록시 이미지 싹 지우기
        Popup proxyPopup = modifyInfo.getProxyPopup();
        List<PosterImage> proxyImages = posterImageRepository.findByPopupId(proxyPopup);
        List<String> proxyUrls = proxyImages.stream()
                .map(PosterImage::getPosterUrl)
                .toList();
        s3Service.deleteMultipleImages(proxyUrls);
        posterImageRepository.deleteAllByPopupId(proxyPopup);

        //새로운 이미지 추가
        List<String> fileUrls = s3Service.uploadPopupPoster(images, originPopup.getId());

        List<PosterImage> posterImages = new ArrayList<>();
        for(String url : fileUrls){
            PosterImage posterImage = PosterImage.builder()
                    .posterUrl(url)
                    .popup(originPopup)
                    .build();
            posterImages.add(posterImage);
        }
        posterImageRepository.saveAll(posterImages);
        originPopup.updatePosterUrl(fileUrls.get(0));

        // 기존 키워드 삭제 및 다시 저장
        alarmKeywordRepository.deleteAll(originPopup.getAlarmKeywords());

        List<AlarmKeyword> alarmKeywords = new ArrayList<>();
        for(String keyword : updateModifyInfoDto.keywords()){
            alarmKeywords.add(AlarmKeyword.builder()
                    .popupId(originPopup)
                    .keyword(keyword)
                    .build());
        }
        alarmKeywordRepository.saveAll(alarmKeywords);

        // 프록시 키워드 삭제
        alarmKeywordRepository.deleteAllByPopupId(proxyPopup);

        //날짜 요청 유효성 검증
        if (updateModifyInfoDto.openDate().isAfter(updateModifyInfoDto.closeDate())) {
            throw new CommonException(ErrorCode.INVALID_DATE_PARAMETER);
        }

        //현재 운영상태 정의
        EOperationStatus operationStatus;
        if (updateModifyInfoDto.openDate().isAfter(LocalDate.now())){
            operationStatus = EOperationStatus.NOTYET;
        } else if (updateModifyInfoDto.closeDate().isBefore(LocalDate.now())) {
            operationStatus = EOperationStatus.TERMINATED;
        }
        else{
            operationStatus = EOperationStatus.OPERATING;
        }

        // 기존 팝업 업데이트
        originPopup.update(
                updateModifyInfoDto.homepageLink(),
                updateModifyInfoDto.name(),
                updateModifyInfoDto.introduce(),
                updateModifyInfoDto.address(),
                updateModifyInfoDto.addressDetail(),
                updateModifyInfoDto.entranceFee(),
                updateModifyInfoDto.resvRequired(),
                updateModifyInfoDto.availableAge(),
                updateModifyInfoDto.parkingAvailable(),
                updateModifyInfoDto.openDate(),
                updateModifyInfoDto.closeDate(),
                updateModifyInfoDto.openTime(),
                updateModifyInfoDto.closeTime(),
                updateModifyInfoDto.operationExcept(),
                operationStatus,
                admin
        );

        modifyInfo.update(true);
        modifyInfo = modifyInformRepository.save(modifyInfo);

        popupRepository.delete(proxyPopup);

        return ModifyInfoDto.fromEntity(modifyInfo, null);
    } // 업로드
}
