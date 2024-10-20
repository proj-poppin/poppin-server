package com.poppin.poppinserver.inform.service;

import com.poppin.poppinserver.alarm.domain.PopupAlarmKeyword;
import com.poppin.poppinserver.alarm.repository.PopupAlarmKeywordRepository;
import com.poppin.poppinserver.core.dto.PageInfoDto;
import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.EInformProgress;
import com.poppin.poppinserver.core.type.EOperationStatus;
import com.poppin.poppinserver.inform.domain.UserInform;
import com.poppin.poppinserver.inform.dto.userInform.request.UpdateUserInformDto;
import com.poppin.poppinserver.inform.dto.userInform.response.UserInformDto;
import com.poppin.poppinserver.inform.dto.userInform.response.UserInformSummaryDto;
import com.poppin.poppinserver.inform.repository.ManagerInformRepository;
import com.poppin.poppinserver.inform.repository.UserInformRepository;
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
import com.poppin.poppinserver.user.repository.UserRepository;
import com.poppin.poppinserver.user.usecase.ReadUserUseCase;
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
public class AdminUserInformService {
    private final UserInformRepository userInformRepository;
    private final TastePopupRepository tastePopupRepository;
    private final PosterImageRepository posterImageRepository;
    private final PopupAlarmKeywordRepository popupAlarmKeywordRepository;
    private final PreferedPopupRepository preferedPopupRepository;

    private final S3Service s3Service;

    private final ReadUserUseCase readUserUseCase;

    @Transactional
    public UserInformDto readUserInform(Long userInformId) {
        UserInform userInform = userInformRepository.findById(userInformId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER_INFORM));

        return UserInformDto.fromEntity(userInform);
    } // 사용자 제보 조회

    @Transactional
    public UserInformDto updateUserInform(UpdateUserInformDto updateUserInformDto,
                                          List<MultipartFile> images,
                                          Long adminId) {
        UserInform userInform = userInformRepository.findById(Long.valueOf(updateUserInformDto.userInformId()))
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER_INFORM));

        User admin = readUserUseCase.findUserById(adminId);

        CreateTasteDto createTasteDto = updateUserInformDto.taste();
        TastePopup tastePopup = userInform.getPopupId().getTastePopup();
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

        CreatePreferedDto createPreferedDto = updateUserInformDto.prefered();
        PreferedPopup preferedPopup = userInform.getPopupId().getPreferedPopup();
        preferedPopup.update(createPreferedDto.market(),
                createPreferedDto.display(),
                createPreferedDto.experience(),
                createPreferedDto.wantFree());
        preferedPopupRepository.save(preferedPopup);

        Popup popup = userInform.getPopupId();

        // 팝업 이미지 처리 및 저장

        // 기존 이미지 싹 지우기
        List<PosterImage> originImages = posterImageRepository.findByPopupId(popup);
        List<String> originUrls = originImages.stream()
                .map(PosterImage::getPosterUrl)
                .collect(Collectors.toList());
        s3Service.deleteMultipleImages(originUrls);
        posterImageRepository.deleteAllByPopupId(popup);

        //새로운 이미지 추가
        List<String> newUrls = s3Service.uploadPopupPoster(images, popup.getId());

        List<PosterImage> posterImages = new ArrayList<>();
        for (String url : newUrls) {
            PosterImage posterImage = PosterImage.builder()
                    .posterUrl(url)
                    .popup(popup)
                    .build();
            posterImages.add(posterImage);
        }
        posterImageRepository.saveAll(posterImages);
        popup.updatePosterUrl(newUrls.get(0));

        // 기존 키워드 삭제 및 다시 저장
        popupAlarmKeywordRepository.deleteAll(popup.getPopupAlarmKeywords());

        List<PopupAlarmKeyword> popupAlarmKeywords = new ArrayList<>();
        for (String keyword : updateUserInformDto.keywords()) {
            popupAlarmKeywords.add(PopupAlarmKeyword.builder()
                    .popupId(popup)
                    .keyword(keyword)
                    .build());
        }
        popupAlarmKeywordRepository.saveAll(popupAlarmKeywords);

        popup.update(
                updateUserInformDto.homepageLink(),
                updateUserInformDto.name(),
                updateUserInformDto.introduce(),
                updateUserInformDto.address(),
                updateUserInformDto.addressDetail(),
                updateUserInformDto.entranceRequired(),
                updateUserInformDto.entranceFee(),
                updateUserInformDto.resvRequired(),
                updateUserInformDto.availableAge(),
                updateUserInformDto.parkingAvailable(),
                updateUserInformDto.openDate(),
                updateUserInformDto.closeDate(),
                updateUserInformDto.openTime(),
                updateUserInformDto.closeTime(),
                updateUserInformDto.latitude(),
                updateUserInformDto.longitude(),
                updateUserInformDto.operationExcept(),
                EOperationStatus.EXECUTING.getStatus(),
                admin
        );

        userInform.update(EInformProgress.EXECUTING);
        userInform = userInformRepository.save(userInform);
        log.info(userInform.getProgress().toString());

        return UserInformDto.fromEntity(userInform);
    } // 임시저장

    @Transactional
    public UserInformDto uploadPopup(UpdateUserInformDto updateUserInformDto,
                                     List<MultipartFile> images,
                                     Long adminId) {
        UserInform userInform = userInformRepository.findById(Long.valueOf(updateUserInformDto.userInformId()))
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER_INFORM));

        User admin = readUserUseCase.findUserById(adminId);

        CreateTasteDto createTasteDto = updateUserInformDto.taste();
        TastePopup tastePopup = userInform.getPopupId().getTastePopup();
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

        CreatePreferedDto createPreferedDto = updateUserInformDto.prefered();
        PreferedPopup preferedPopup = userInform.getPopupId().getPreferedPopup();
        preferedPopup.update(createPreferedDto.market(),
                createPreferedDto.display(),
                createPreferedDto.experience(),
                createPreferedDto.wantFree());
        preferedPopupRepository.save(preferedPopup);

        Popup popup = userInform.getPopupId();

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
        for (String keyword : updateUserInformDto.keywords()) {
            popupAlarmKeywords.add(PopupAlarmKeyword.builder()
                    .popupId(popup)
                    .keyword(keyword)
                    .build());
        }
        popupAlarmKeywordRepository.saveAll(popupAlarmKeywords);

        //날짜 요청 유효성 검증
        if (updateUserInformDto.openDate().isAfter(updateUserInformDto.closeDate())) {
            throw new CommonException(ErrorCode.INVALID_DATE_PARAMETER);
        }

        //현재 운영상태 정의
        String operationStatus;
        if (updateUserInformDto.openDate().isAfter(LocalDate.now())) {
            operationStatus = EOperationStatus.NOTYET.getStatus();
        } else if (updateUserInformDto.closeDate().isBefore(LocalDate.now())) {
            operationStatus = EOperationStatus.TERMINATED.getStatus();
        } else {
            operationStatus = EOperationStatus.OPERATING.getStatus();
        }

        // 입장료 유무 false일 경우, 입장료 무료
        String entranceFee = updateUserInformDto.entranceFee();
        if (!updateUserInformDto.entranceRequired()) {
            entranceFee = "무료";
        }

        popup.update(
                updateUserInformDto.homepageLink(),
                updateUserInformDto.name(),
                updateUserInformDto.introduce(),
                updateUserInformDto.address(),
                updateUserInformDto.addressDetail(),
                updateUserInformDto.entranceRequired(),
                entranceFee,
                updateUserInformDto.resvRequired(),
                updateUserInformDto.availableAge(),
                updateUserInformDto.parkingAvailable(),
                updateUserInformDto.openDate(),
                updateUserInformDto.closeDate(),
                updateUserInformDto.openTime(),
                updateUserInformDto.closeTime(),
                updateUserInformDto.latitude(),
                updateUserInformDto.longitude(),
                updateUserInformDto.operationExcept(),
                operationStatus,
                admin
        );

        userInform.update(EInformProgress.EXECUTED);
        userInform = userInformRepository.save(userInform);

        return UserInformDto.fromEntity(userInform);
    } // 제보 최종 업로그

    @Transactional
    public PagingResponseDto readUserInformList(int page,
                                                int size,
                                                EInformProgress progress) {
        Page<UserInform> userInforms = userInformRepository.findAllByProgress(PageRequest.of(page, size), progress);

        PageInfoDto pageInfoDto = PageInfoDto.fromPageInfo(userInforms);
        List<UserInformSummaryDto> userInformSummaryDtos = UserInformSummaryDto.fromEntityList(
                userInforms.getContent());

        return PagingResponseDto.fromEntityAndPageInfo(userInformSummaryDtos, pageInfoDto);
    } // 제보 리스트 조회
}
