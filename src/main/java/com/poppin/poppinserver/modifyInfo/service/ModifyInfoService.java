package com.poppin.poppinserver.modifyInfo.service;

import com.poppin.poppinserver.alarm.domain.PopupAlarmKeyword;
import com.poppin.poppinserver.alarm.repository.PopupAlarmKeywordRepository;
import com.poppin.poppinserver.core.dto.PageInfoDto;
import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.inform.repository.ModifyInformRepository;
import com.poppin.poppinserver.modifyInfo.dto.modifyInfo.request.CreateModifyInfoDto;
import com.poppin.poppinserver.modifyInfo.dto.modifyInfo.request.UpdateModifyInfoDto;
import com.poppin.poppinserver.modifyInfo.dto.modifyInfo.response.ModifyInfoDto;
import com.poppin.poppinserver.modifyInfo.dto.modifyInfo.response.ModifyInfoSummaryDto;
import com.poppin.poppinserver.modifyInfo.repository.ModifyImageReposiroty;
import com.poppin.poppinserver.popup.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.popup.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.modifyInfo.domain.ModifyImages;
import com.poppin.poppinserver.modifyInfo.domain.ModifyInfo;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.popup.domain.PreferedPopup;
import com.poppin.poppinserver.popup.domain.TastePopup;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.popup.repository.PosterImageRepository;
import com.poppin.poppinserver.popup.repository.PreferedPopupRepository;
import com.poppin.poppinserver.popup.repository.TastePopupRepository;
import com.poppin.poppinserver.core.type.EOperationStatus;
import com.poppin.poppinserver.popup.service.S3Service;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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
    private final PopupAlarmKeywordRepository popupAlarmKeywordRepository;

    private final S3Service s3Service;

    @Transactional
    public ModifyInfoDto createModifyInfo(CreateModifyInfoDto createModifyInfoDto,
                                          List<MultipartFile> images,
                                          Long userId) {
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
                .alcohol(tastePopup.getAlcohol())
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
                .entranceRequired(popup.getEntranceRequired())
                .entranceFee(popup.getEntranceFee())
                .resvRequired(popup.getResvRequired())
                .introduce(popup.getIntroduce())
                .address(popup.getAddress())
                .addressDetail(popup.getAddressDetail())
                .openDate(popup.getOpenDate())
                .openTime(popup.getOpenTime())
                .latitude(popup.getLatitude())
                .longitude(popup.getLongitude())
                .operationExcept(popup.getOperationExcept())
                .operationStatus(EOperationStatus.EXECUTING.getStatus())
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
        for (String proxyUrl : proxyUrls) {
            PosterImage proxyImage = PosterImage.builder()
                    .posterUrl(proxyUrl)
                    .popup(proxyPopup)
                    .build();
            proxyImages.add(proxyImage);
        }
        posterImageRepository.saveAll(proxyImages);
        proxyPopup.updatePosterUrl(proxyImages.get(0).getPosterUrl());

        // 프록시 알람키워드 생성
        List<PopupAlarmKeyword> popupAlarmKeywords = popupAlarmKeywordRepository.findByPopupId(popup);

        List<PopupAlarmKeyword> proxyKeywords = new ArrayList<>();
        for (PopupAlarmKeyword popupAlarmKeyword : popupAlarmKeywords) {
            PopupAlarmKeyword proxyKeyword = PopupAlarmKeyword.builder()
                    .popupId(proxyPopup)
                    .keyword(popupAlarmKeyword.getKeyword())
                    .build();
        }
        popupAlarmKeywordRepository.saveAll(proxyKeywords);

        // 정보수정요청 객체 저장
        ModifyInfo modifyInfo = ModifyInfo.builder()
                .content(createModifyInfoDto.content())
                .userId(user)
                .proxyPopup(proxyPopup)
                .originPopup(popup)
                .build();
        modifyInformRepository.save(modifyInfo);

        // 정보수정요청 이미지 저장
        String imageStatus = "0";

        List<String> fileUrls = new ArrayList<>();
        if (!images.get(0).getOriginalFilename().equals("empty")) {

            log.info("images Entity : " + images);
            log.info("images Size : " + images.size());
            log.info("images first img name: " + images.get(0).getOriginalFilename());

            imageStatus = "1"; // 이미지가 null 이 아닐때

            // 리뷰 이미지 처리 및 저장
            fileUrls = s3Service.uploadModifyInfo(images, modifyInfo.getId());

            List<ModifyImages> modifyImagesList = new ArrayList<>();
            for (String url : fileUrls) {
                ModifyImages modifyImage = ModifyImages.builder()
                        .modifyId(modifyInfo)
                        .imageUrl(url)
                        .build();
                modifyImagesList.add(modifyImage);
            }
            modifyImageReposiroty.saveAll(modifyImagesList);
        }

        return ModifyInfoDto.fromEntity(modifyInfo, fileUrls);
    } // 요청 생성

    @Transactional
    public ModifyInfoDto readModifyInfo(Long modifyInfoId, Long adminId) {
        ModifyInfo modifyInfo = modifyInformRepository.findById(modifyInfoId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MODIFY_INFO));

        User user = userRepository.findById(modifyInfo.getUserId().getId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        List<ModifyImages> modifyImageList = modifyImageReposiroty.findByModifyId(modifyInfo);

        List<String> imageList = new ArrayList<>();
        for (ModifyImages modifyImages : modifyImageList) {
            imageList.add(modifyImages.getImageUrl());
        }

        PopupDto popupDto = null;
        if (modifyInfo != null) {
            popupDto = PopupDto.fromEntity(modifyInfo.getProxyPopup());
        }

        String agentName = null;
        if (modifyInfo.getIsExecuted()) {
            agentName = modifyInfo.getOriginPopup().getAgent().getNickname();
        } else {
            User admin = userRepository.findById(adminId)
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
            agentName = admin.getNickname();
        }

        return ModifyInfoDto.builder()
                .id(modifyInfo.getId())
                .userId(modifyInfo.getId())
                .userImageUrl(modifyInfo.getUserId().getProfileImageUrl())
                .email(modifyInfo.getUserId().getEmail())
                .nickname(modifyInfo.getUserId().getNickname())
                .popup(popupDto)
                .popupName(popupDto.name())
                .createdAt(modifyInfo.getCreatedAt().toString())
                .content(modifyInfo.getContent())
                .info(modifyInfo.getInfo())
                .agentName(agentName)
                .isExecuted(modifyInfo.getIsExecuted())
                .images(imageList)
                .build();
    } // 조회

    @Transactional
    public PagingResponseDto readModifyInfoList(int page, int size, Boolean isExec) {
        Page<ModifyInfo> modifyInfoList = modifyInformRepository.findAllByIsExecuted(PageRequest.of(page, size),
                isExec);

        PageInfoDto pageInfoDto = PageInfoDto.fromPageInfo(modifyInfoList);
        List<ModifyInfoSummaryDto> modifyInfoSummaryDtos = ModifyInfoSummaryDto.fromEntityList(
                modifyInfoList.getContent());

        return PagingResponseDto.fromEntityAndPageInfo(modifyInfoSummaryDtos, pageInfoDto);
    }// 목록 조회

    @Transactional
    public ModifyInfoDto updateModifyInfo(UpdateModifyInfoDto updateModifyInfoDto,
                                          List<MultipartFile> images,
                                          Long adminId) {
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
        if (originUrls.size() != 0) {
            s3Service.deleteMultipleImages(originUrls);
            posterImageRepository.deleteAllByPopupId(popup);
        }

        //새로운 이미지 추가
        List<String> fileUrls = new ArrayList<>();
        if (images.get(0).getOriginalFilename() != "") { // 이미지가 비었을 시 넘어감
            fileUrls = s3Service.uploadPopupPoster(images, popup.getId());

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
        }

        // 기존 키워드 삭제 및 다시 저장
        popupAlarmKeywordRepository.deleteAll(popup.getPopupAlarmKeywords());

        List<PopupAlarmKeyword> popupAlarmKeywords = new ArrayList<>();
        for (String keyword : updateModifyInfoDto.keywords()) {
            popupAlarmKeywords.add(PopupAlarmKeyword.builder()
                    .popupId(popup)
                    .keyword(keyword)
                    .build());
        }
        popupAlarmKeywordRepository.saveAll(popupAlarmKeywords);

        popup.update(
                updateModifyInfoDto.homepageLink(),
                updateModifyInfoDto.name(),
                updateModifyInfoDto.introduce(),
                updateModifyInfoDto.address(),
                updateModifyInfoDto.addressDetail(),
                updateModifyInfoDto.entranceRequired(),
                updateModifyInfoDto.entranceFee(),
                updateModifyInfoDto.resvRequired(),
                updateModifyInfoDto.availableAge(),
                updateModifyInfoDto.parkingAvailable(),
                updateModifyInfoDto.openDate(),
                updateModifyInfoDto.closeDate(),
                updateModifyInfoDto.openTime(),
                updateModifyInfoDto.closeTime(),
                updateModifyInfoDto.latitude(),
                updateModifyInfoDto.longitude(),
                updateModifyInfoDto.operationExcept(),
                EOperationStatus.EXECUTING.getStatus(),
                admin
        );

        modifyInfo.update(updateModifyInfoDto.info());

        modifyInfo = modifyInformRepository.save(modifyInfo);

        return ModifyInfoDto.fromEntity(modifyInfo, null);
    } // 임시 저장

    @Transactional
    public ModifyInfoDto uploadModifyInfo(UpdateModifyInfoDto updateModifyInfoDto,
                                          List<MultipartFile> images,
                                          Long adminId) {
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
        if (originUrls.size() != 0) {
            s3Service.deleteMultipleImages(originUrls);
            posterImageRepository.deleteAllByPopupId(originPopup);
        }

        // 프록시 이미지 싹 지우기
        Popup proxyPopup = modifyInfo.getProxyPopup();
        List<PosterImage> proxyImages = posterImageRepository.findByPopupId(proxyPopup);
        List<String> proxyUrls = proxyImages.stream()
                .map(PosterImage::getPosterUrl)
                .toList();
        if (proxyUrls.size() != 0) {
            s3Service.deleteMultipleImages(proxyUrls);
            posterImageRepository.deleteAllByPopupId(proxyPopup);
        }

        //새로운 이미지 추가
        List<String> fileUrls = new ArrayList<>();
        if (images.get(0).getOriginalFilename() != "") { // 이미지가 비었을 시 넘어감
            fileUrls = s3Service.uploadPopupPoster(images, originPopup.getId());

            List<PosterImage> posterImages = new ArrayList<>();
            for (String url : fileUrls) {
                PosterImage posterImage = PosterImage.builder()
                        .posterUrl(url)
                        .popup(originPopup)
                        .build();
                posterImages.add(posterImage);
            }
            posterImageRepository.saveAll(posterImages);
            originPopup.updatePosterUrl(fileUrls.get(0));
        }

        // 기존 키워드 삭제 및 다시 저장
        popupAlarmKeywordRepository.deleteAll(originPopup.getPopupAlarmKeywords());

        List<PopupAlarmKeyword> popupAlarmKeywords = new ArrayList<>();
        for (String keyword : updateModifyInfoDto.keywords()) {
            popupAlarmKeywords.add(PopupAlarmKeyword.builder()
                    .popupId(originPopup)
                    .keyword(keyword)
                    .build());
        }
        popupAlarmKeywordRepository.saveAll(popupAlarmKeywords);

        // 프록시 키워드 삭제
        popupAlarmKeywordRepository.deleteAllByPopupId(proxyPopup);

        //날짜 요청 유효성 검증
        if (updateModifyInfoDto.openDate().isAfter(updateModifyInfoDto.closeDate())) {
            throw new CommonException(ErrorCode.INVALID_DATE_PARAMETER);
        }

        //현재 운영상태 정의
        String operationStatus;
        if (updateModifyInfoDto.openDate().isAfter(LocalDate.now())) {
            operationStatus = EOperationStatus.NOTYET.getStatus();
        } else if (updateModifyInfoDto.closeDate().isBefore(LocalDate.now())) {
            operationStatus = EOperationStatus.TERMINATED.getStatus();
        } else {
            operationStatus = EOperationStatus.OPERATING.getStatus();
        }

        // 입장료 유무 false일 경우, 입장료 무료
        String entranceFee = updateModifyInfoDto.entranceFee();
        if (!updateModifyInfoDto.entranceRequired()) {
            entranceFee = "무료";
        }

        // 기존 팝업 업데이트
        originPopup.update(
                updateModifyInfoDto.homepageLink(),
                updateModifyInfoDto.name(),
                updateModifyInfoDto.introduce(),
                updateModifyInfoDto.address(),
                updateModifyInfoDto.addressDetail(),
                updateModifyInfoDto.entranceRequired(),
                entranceFee,
                updateModifyInfoDto.resvRequired(),
                updateModifyInfoDto.availableAge(),
                updateModifyInfoDto.parkingAvailable(),
                updateModifyInfoDto.openDate(),
                updateModifyInfoDto.closeDate(),
                updateModifyInfoDto.openTime(),
                updateModifyInfoDto.closeTime(),
                updateModifyInfoDto.latitude(),
                updateModifyInfoDto.longitude(),
                updateModifyInfoDto.operationExcept(),
                operationStatus,
                admin
        );

        modifyInfo.update(updateModifyInfoDto.info(), true);
        modifyInfo = modifyInformRepository.save(modifyInfo);

        popupRepository.delete(proxyPopup);

        return ModifyInfoDto.fromEntity(modifyInfo, null);
    } // 업로드

    @Transactional
    public void deleteProxyPopupAndModifyInfoByPopupId(Long popupId) {
        log.info("delete modify info data");
        List<ModifyInfo> modifyInfoList = modifyInformRepository.findAllByOriginPopupId(popupId);

        for (ModifyInfo modifyInfo : modifyInfoList) {
            // proxy popup 삭제
            Popup proxyPopup = modifyInfo.getProxyPopup();

            // modify info 삭제
            log.info("delete modify info");
            // modify info 이미지 삭제
            List<ModifyImages> modifyImages = modifyImageReposiroty.findByModifyId(modifyInfo);
            List<String> modifyUrls = modifyImages.stream()
                    .map(ModifyImages::getImageUrl)
                    .toList();
            if (modifyUrls.size() != 0) {
                s3Service.deleteMultipleImages(modifyUrls);
                modifyImageReposiroty.deleteAllByModifyId(modifyInfo);
            }
            // modify info 삭제
            modifyInformRepository.delete(modifyInfo);

            // proxy popup 이미지 삭제
            List<PosterImage> proxyImages = posterImageRepository.findAllByPopupId(proxyPopup);
            List<String> proxyUrls = proxyImages.stream()
                    .map(PosterImage::getPosterUrl)
                    .toList();
            if (proxyUrls.size() != 0) {
                s3Service.deleteMultipleImages(proxyUrls);
                posterImageRepository.deleteAllByPopupId(proxyPopup);
            }

            // proxy popup 알람 키워드 삭제
            popupAlarmKeywordRepository.deleteAllByPopupId(proxyPopup);

            // proxy popup 삭제
            log.info("delete proxy popup");
            popupRepository.delete(proxyPopup);


        }
    } // 프록시 팝업 삭제 및 정보수정요청 삭제
}
