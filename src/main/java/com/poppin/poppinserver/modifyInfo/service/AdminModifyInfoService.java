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
import com.poppin.poppinserver.popup.dto.popup.response.AdminPopupDto;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.popup.repository.PosterImageRepository;
import com.poppin.poppinserver.popup.service.S3Service;
import com.poppin.poppinserver.popup.usecase.PopupCommandUseCase;
import com.poppin.poppinserver.popup.usecase.PosterImageCommandUseCase;
import com.poppin.poppinserver.popup.usecase.PreferedPopupCommandUseCase;
import com.poppin.poppinserver.popup.usecase.TastedPopupCommandUseCase;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminModifyInfoService {
    private final ModifyInformRepository modifyInformRepository;
    private final ModifyImageReposiroty modifyImageReposiroty;
    private final PopupAlarmKeywordRepository popupAlarmKeywordRepository;

    private final S3Service s3Service;

    private final UserQueryUseCase userQueryUseCase;
    private final PreferedPopupCommandUseCase preferedPopupCommandUseCase;
    private final TastedPopupCommandUseCase tastedPopupCommandUseCase;
    private final PopupCommandUseCase popupCommandUseCase;
    private final PosterImageCommandUseCase posterImageCommandUseCase;

    @Transactional
    public AdminModifyInfoDto readModifyInfo(Long modifyInfoId, Long adminId) {
        ModifyInfo modifyInfo = modifyInformRepository.findById(modifyInfoId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MODIFY_INFO));

        User user = userQueryUseCase.findUserById(modifyInfo.getUserId().getId());

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
            User admin = userQueryUseCase.findUserById(adminId);
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
    public PagingResponseDto<List<ModifyInfoSummaryDto>> readModifyInfoList(int page, int size, Boolean isExec) {
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
        User admin = userQueryUseCase.findUserById(adminId);

        ModifyInfo modifyInfo = modifyInformRepository.findById(updateModifyInfoDto.modifyInfoId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MODIFY_INFO));

        // 카테고리 업데이트
        tastedPopupCommandUseCase.updateTastePopup(modifyInfo.getProxyPopup().getTastePopup(), updateModifyInfoDto.taste());
        preferedPopupCommandUseCase.updatePreferedPopup(modifyInfo.getProxyPopup().getPreferedPopup(), updateModifyInfoDto.prefered());

        Popup popup = modifyInfo.getProxyPopup();

        // 팝업 이미지 처리 및 저장

        // 기존 이미지 싹 지우기
        posterImageCommandUseCase.deletePosterList(popup);

        //새로운 이미지 추가
        if (images.get(0).getOriginalFilename() != "") { // 이미지가 비었을 시 넘어감
            // 팝업 이미지 처리 및 저장
            List<PosterImage> posterImages = posterImageCommandUseCase.savePosterList(images, popup);

            // 대표사진 저장
            popupCommandUseCase.updatePopupPosterUrl(popup, posterImages.get(0));
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
        User admin = userQueryUseCase.findUserById(adminId);

        ModifyInfo modifyInfo = modifyInformRepository.findById(updateModifyInfoDto.modifyInfoId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MODIFY_INFO));

        // 기존 팝업에 수정 사항 덮어 씌우기

        // 카테고리 덮어 씌우기
        tastedPopupCommandUseCase.updateTastePopup(modifyInfo.getOriginPopup().getTastePopup(), updateModifyInfoDto.taste());
        preferedPopupCommandUseCase.updatePreferedPopup(modifyInfo.getOriginPopup().getPreferedPopup(), updateModifyInfoDto.prefered());

        // 팝업 이미지 처리 및 저장
        // 기존 이미지 싹 지우기
        Popup originPopup = modifyInfo.getOriginPopup();
        posterImageCommandUseCase.deletePosterList(originPopup);

        // 프록시 이미지 싹 지우기
        Popup proxyPopup = modifyInfo.getProxyPopup();
        posterImageCommandUseCase.deletePosterList(originPopup);

        //새로운 이미지 추가
        if (images.get(0).getOriginalFilename() != "") { // 이미지가 비었을 시 넘어감
            // 팝업 이미지 처리 및 저장
            List<PosterImage> posterImages = posterImageCommandUseCase.savePosterList(images, originPopup);

            // 대표사진 저장
            popupCommandUseCase.updatePopupPosterUrl(originPopup, posterImages.get(0));
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

        popupCommandUseCase.deletePopup(proxyPopup);

        return AdminModifyInfoDto.fromEntity(modifyInfo, null);
    } // 업로드
}
