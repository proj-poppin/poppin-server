package com.poppin.poppinserver.modifyInfo.service;

import com.poppin.poppinserver.alarm.domain.PopupAlarmKeyword;
import com.poppin.poppinserver.alarm.repository.PopupAlarmKeywordRepository;
import com.poppin.poppinserver.core.dto.PageInfoDto;
import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.inform.repository.ModifyInformRepository;
import com.poppin.poppinserver.modifyInfo.dto.request.CreateModifyInfoDto;
import com.poppin.poppinserver.modifyInfo.dto.request.UpdateModifyInfoDto;
import com.poppin.poppinserver.modifyInfo.dto.response.AdminModifyInfoDto;
import com.poppin.poppinserver.modifyInfo.dto.response.ModifyInfoDto;
import com.poppin.poppinserver.modifyInfo.dto.response.ModifyInfoSummaryDto;
import com.poppin.poppinserver.modifyInfo.repository.ModifyImageReposiroty;
import com.poppin.poppinserver.popup.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.popup.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.popup.dto.popup.response.AdminPopupDto;
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
