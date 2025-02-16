package com.poppin.poppinserver.inform.service;

import com.poppin.poppinserver.alarm.domain.PopupAlarmKeyword;
import com.poppin.poppinserver.alarm.repository.PopupAlarmKeywordRepository;
import com.poppin.poppinserver.core.dto.PageInfoDto;
import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.EInformProgress;
import com.poppin.poppinserver.core.type.EOperationStatus;
import com.poppin.poppinserver.inform.domain.ManagerInform;
import com.poppin.poppinserver.inform.dto.managerInform.request.UpdateManagerInformDto;
import com.poppin.poppinserver.inform.dto.managerInform.response.ManagerInformDto;
import com.poppin.poppinserver.inform.dto.managerInform.response.ManagerInformSummaryDto;
import com.poppin.poppinserver.inform.repository.ManagerInformRepository;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.popup.repository.PosterImageRepository;
import com.poppin.poppinserver.popup.service.S3Service;
import com.poppin.poppinserver.popup.usecase.PreferedPopupCommandUseCase;
import com.poppin.poppinserver.popup.usecase.TastedPopupCommandUseCase;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import java.time.LocalDate;
import java.time.Period;
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
public class AdminManagerInformService {
    // 주석
    private final ManagerInformRepository managerInformRepository;
    private final PopupRepository popupRepository;
    private final PosterImageRepository posterImageRepository;
    private final PopupAlarmKeywordRepository popupAlarmKeywordRepository;

    private final S3Service s3Service;

    private final UserQueryUseCase userQueryUseCase;
    private final PreferedPopupCommandUseCase preferedPopupCommandUseCase;
    private final TastedPopupCommandUseCase tastedPopupCommandUseCase;

    @Transactional
    public ManagerInformDto readManageInform(Long manageInformId) {
        ManagerInform managerInform = managerInformRepository.findById(manageInformId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MANAGE_INFORM));

        return ManagerInformDto.fromEntity(managerInform);
    } // 운영자 제보 조회

    @Transactional
    public ManagerInformDto updateManageInform(UpdateManagerInformDto updateManagerInformDto,
                                               List<MultipartFile> images,
                                               Long adminId) {
        ManagerInform managerInform = managerInformRepository.findById(
                        Long.valueOf(updateManagerInformDto.managerInformId()))
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MANAGE_INFORM));

        // 관리자 검증
        User admin = userQueryUseCase.findUserById(adminId);

        // 카테고리 업데이트
        tastedPopupCommandUseCase.updateTastePopup(managerInform.getPopupId().getTastePopup(), updateManagerInformDto.taste());
        preferedPopupCommandUseCase.updatePreferedPopup(managerInform.getPopupId().getPreferedPopup(), updateManagerInformDto.prefered());

        Popup popup = managerInform.getPopupId();

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
        for (String url : fileUrls) {
            PosterImage posterImage = PosterImage.builder()
                    .posterUrl(url)
                    .popup(popup)
                    .build();
            posterImages.add(posterImage);
        }
        posterImageRepository.saveAll(posterImages);
        popup.updatePosterUrl(fileUrls.get(0));

        // 기존 키워드 삭제 및 다시 저장
        popupAlarmKeywordRepository.deleteAll(popup.getPopupAlarmKeywords());

        List<PopupAlarmKeyword> popupAlarmKeywords = new ArrayList<>();
        for (String keyword : updateManagerInformDto.keywords()) {
            popupAlarmKeywords.add(PopupAlarmKeyword.builder()
                    .popupId(popup)
                    .keyword(keyword)
                    .build());
        }
        popupAlarmKeywordRepository.saveAll(popupAlarmKeywords);

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
                EOperationStatus.EXECUTING.getStatus(),
                admin
        );

        popup = popupRepository.save(popup);

        managerInform.update(
                EInformProgress.EXECUTING,
                updateManagerInformDto.affiliation(),
                updateManagerInformDto.informerEmail()
        );
        managerInform = managerInformRepository.save(managerInform);
        log.info(managerInform.getProgress().toString());

        return ManagerInformDto.fromEntity(managerInform);
    } // 운영자 제보 임시저장

    @Transactional
    public ManagerInformDto uploadPopup(UpdateManagerInformDto updateManagerInformDto,
                                        List<MultipartFile> images,
                                        Long adminId) {
        ManagerInform managerInform = managerInformRepository.findById(
                        Long.valueOf(updateManagerInformDto.managerInformId()))
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_MANAGE_INFORM));

        // 관리자 검증
        User admin = userQueryUseCase.findUserById(adminId);

        // 카테고리 업데이트
        tastedPopupCommandUseCase.updateTastePopup(managerInform.getPopupId().getTastePopup(), updateManagerInformDto.taste());
        preferedPopupCommandUseCase.updatePreferedPopup(managerInform.getPopupId().getPreferedPopup(), updateManagerInformDto.prefered());

        Popup popup = managerInform.getPopupId();

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
        for (String url : fileUrls) {
            PosterImage posterImage = PosterImage.builder()
                    .posterUrl(url)
                    .popup(popup)
                    .build();
            posterImages.add(posterImage);
        }
        posterImageRepository.saveAll(posterImages);
        popup.updatePosterUrl(fileUrls.get(0));

        // 기존 키워드 삭제 및 다시 저장
        popupAlarmKeywordRepository.deleteAll(popup.getPopupAlarmKeywords());

        List<PopupAlarmKeyword> popupAlarmKeywords = new ArrayList<>();
        for (String keyword : updateManagerInformDto.keywords()) {
            popupAlarmKeywords.add(PopupAlarmKeyword.builder()
                    .popupId(popup)
                    .keyword(keyword)
                    .build());
        }
        popupAlarmKeywordRepository.saveAll(popupAlarmKeywords);

        //날짜 요청 유효성 검증
        if (updateManagerInformDto.openDate().isAfter(updateManagerInformDto.closeDate())) {
            throw new CommonException(ErrorCode.INVALID_DATE_PARAMETER);
        }

        //현재 운영상태 정의
        String operationStatus;
        if (updateManagerInformDto.openDate().isAfter(LocalDate.now())) {
            Period period = Period.between(LocalDate.now(), updateManagerInformDto.openDate());
            operationStatus = EOperationStatus.NOTYET.getStatus();
        } else if (updateManagerInformDto.closeDate().isBefore(LocalDate.now())) {
            operationStatus = EOperationStatus.TERMINATED.getStatus();
        } else {
            operationStatus = EOperationStatus.OPERATING.getStatus();
        }

        // 입장료 유무 false일 경우, 입장료 무료
        String entranceFee = updateManagerInformDto.entranceFee();
        if (!updateManagerInformDto.entranceRequired()) {
            entranceFee = "무료";
        }

        popup.update(
                updateManagerInformDto.homepageLink(),
                updateManagerInformDto.name(),
                updateManagerInformDto.introduce(),
                updateManagerInformDto.address(),
                updateManagerInformDto.addressDetail(),
                updateManagerInformDto.entranceRequired(),
                entranceFee,
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
                admin
        );

        managerInform.update(
                EInformProgress.EXECUTED,
                updateManagerInformDto.affiliation(),
                updateManagerInformDto.informerEmail()
        );
        managerInform = managerInformRepository.save(managerInform);

        return ManagerInformDto.fromEntity(managerInform);
    } // 운영자 제보 업로드 승인

    @Transactional
    public PagingResponseDto<List<ManagerInformSummaryDto>> readManagerInformList(int page, int size,
                                                                                  EInformProgress progress) {
        Page<ManagerInform> managerInforms = managerInformRepository.findAllByProgress(PageRequest.of(page, size),
                progress);

        PageInfoDto pageInfoDto = PageInfoDto.fromPageInfo(managerInforms);
        List<ManagerInformSummaryDto> userInformSummaryDtos = ManagerInformSummaryDto.fromEntityList(
                managerInforms.getContent());

        return PagingResponseDto.fromEntityAndPageInfo(userInformSummaryDtos, pageInfoDto);
    } // 제보 리스트 조회
}
