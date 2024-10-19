package com.poppin.poppinserver.modifyInfo.service;

import com.poppin.poppinserver.alarm.domain.PopupAlarmKeyword;
import com.poppin.poppinserver.alarm.repository.PopupAlarmKeywordRepository;
import com.poppin.poppinserver.core.dto.PageInfoDto;
import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.EOperationStatus;
import com.poppin.poppinserver.inform.repository.ModifyInformRepository;
import com.poppin.poppinserver.modifyInfo.domain.ModifyImages;
import com.poppin.poppinserver.modifyInfo.domain.ModifyInfo;
import com.poppin.poppinserver.modifyInfo.dto.request.UpdateModifyInfoDto;
import com.poppin.poppinserver.modifyInfo.dto.response.AdminModifyInfoDto;
import com.poppin.poppinserver.modifyInfo.dto.response.ModifyInfoSummaryDto;
import com.poppin.poppinserver.modifyInfo.repository.ModifyImageReposiroty;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.popup.domain.PreferedPopup;
import com.poppin.poppinserver.popup.domain.TastePopup;
import com.poppin.poppinserver.popup.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.popup.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.popup.dto.popup.response.AdminPopupDto;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.popup.repository.PosterImageRepository;
import com.poppin.poppinserver.popup.repository.PreferedPopupRepository;
import com.poppin.poppinserver.popup.repository.TastePopupRepository;
import com.poppin.poppinserver.popup.service.S3Service;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminModifyInfoService {
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
    public AdminModifyInfoDto readModifyInfo(Long modifyInfoId, Long adminId) {
        ModifyInfo modifyInfo = modifyInformRepository.findById(modifyInfoId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MODIFY_INFO));

        User user = userRepository.findById(modifyInfo.getUserId().getId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        List<ModifyImages> modifyImageList = modifyImageReposiroty.findByModifyId(modifyInfo);

        List<String> imageList = new ArrayList<>();
        for (ModifyImages modifyImages : modifyImageList) {
            imageList.add(modifyImages.getImageUrl());
        }

        AdminPopupDto adminPopupDto = null;
        if (modifyInfo != null) {
            adminPopupDto = AdminPopupDto.fromEntity(modifyInfo.getProxyPopup());
        }

        String agentName = null;
        if (modifyInfo.getIsExecuted()) {
            agentName = modifyInfo.getOriginPopup().getAgent().getNickname();
        } else {
            User admin = userRepository.findById(adminId)
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
            agentName = admin.getNickname();
        }

        return AdminModifyInfoDto.builder()
                .id(modifyInfo.getId())
                .userId(modifyInfo.getId())
                .userImageUrl(modifyInfo.getUserId().getProfileImageUrl())
                .email(modifyInfo.getUserId().getEmail())
                .nickname(modifyInfo.getUserId().getNickname())
                .popup(adminPopupDto)
                .popupName(adminPopupDto.name())
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
    public AdminModifyInfoDto updateModifyInfo(UpdateModifyInfoDto updateModifyInfoDto,
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

        return AdminModifyInfoDto.fromEntity(modifyInfo, null);
    } // 임시 저장

    @Transactional
    public AdminModifyInfoDto uploadModifyInfo(UpdateModifyInfoDto updateModifyInfoDto,
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

        return AdminModifyInfoDto.fromEntity(modifyInfo, null);
    } // 업로드


}
