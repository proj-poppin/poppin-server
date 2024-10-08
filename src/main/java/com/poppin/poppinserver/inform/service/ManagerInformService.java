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
import com.poppin.poppinserver.inform.dto.managerInform.request.CreateManagerInformDto;
import com.poppin.poppinserver.inform.dto.managerInform.request.UpdateManagerInformDto;
import com.poppin.poppinserver.inform.dto.managerInform.response.ManagerInformDto;
import com.poppin.poppinserver.inform.dto.managerInform.response.ManagerInformSummaryDto;
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
import com.poppin.poppinserver.user.repository.UserRepository;
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
public class ManagerInformService {
    private final ManagerInformRepository managerInformRepository;
    private final PopupRepository popupRepository;
    private final TastePopupRepository tastePopupRepository;
    private final PosterImageRepository posterImageRepository;
    private final UserRepository userRepository;
    private final PopupAlarmKeywordRepository popupAlarmKeywordRepository;
    private final PreferedPopupRepository preferedPopupRepository;

    private final S3Service s3Service;

    @Transactional
    public ManagerInformDto createManagerInform(CreateManagerInformDto createManagerInformDto,
                                                List<MultipartFile> images,
                                                Long userId) {
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

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        CreateTasteDto createTasteDto = updateManagerInformDto.taste();
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
                createTasteDto.animalPlant(),
                createTasteDto.etc());
        tastePopupRepository.save(tastePopup);

        CreatePreferedDto createPreferedDto = updateManagerInformDto.prefered();
        PreferedPopup preferedPopup = managerInform.getPopupId().getPreferedPopup();
        preferedPopup.update(createPreferedDto.market(),
                createPreferedDto.display(),
                createPreferedDto.experience(),
                createPreferedDto.wantFree());
        preferedPopupRepository.save(preferedPopup);

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

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        CreateTasteDto createTasteDto = updateManagerInformDto.taste();
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
                createTasteDto.animalPlant(),
                createTasteDto.etc());
        tastePopupRepository.save(tastePopup);

        CreatePreferedDto createPreferedDto = updateManagerInformDto.prefered();
        PreferedPopup preferedPopup = managerInform.getPopupId().getPreferedPopup();
        preferedPopup.update(createPreferedDto.market(),
                createPreferedDto.display(),
                createPreferedDto.experience(),
                createPreferedDto.wantFree());
        preferedPopupRepository.save(preferedPopup);

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
    public PagingResponseDto readManagerInformList(int page, int size, EInformProgress progress) {
        Page<ManagerInform> managerInforms = managerInformRepository.findAllByProgress(PageRequest.of(page, size),
                progress);

        PageInfoDto pageInfoDto = PageInfoDto.fromPageInfo(managerInforms);
        List<ManagerInformSummaryDto> userInformSummaryDtos = ManagerInformSummaryDto.fromEntityList(
                managerInforms.getContent());

        return PagingResponseDto.fromEntityAndPageInfo(userInformSummaryDtos, pageInfoDto);
    } // 제보 리스트 조회
}
