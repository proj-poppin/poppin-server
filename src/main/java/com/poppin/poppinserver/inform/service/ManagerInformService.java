package com.poppin.poppinserver.inform.service;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.EInformProgress;
import com.poppin.poppinserver.core.type.EOperationStatus;
import com.poppin.poppinserver.inform.domain.ManagerInform;
import com.poppin.poppinserver.inform.dto.managerInform.request.CreateManagerInformDto;
import com.poppin.poppinserver.inform.dto.managerInform.response.ManagerInformDto;
import com.poppin.poppinserver.inform.repository.ManagerInformRepository;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.popup.domain.PreferedPopup;
import com.poppin.poppinserver.popup.domain.TastePopup;
import com.poppin.poppinserver.popup.usecase.PopupCommandUseCase;
import com.poppin.poppinserver.popup.usecase.PosterImageCommandUseCase;
import com.poppin.poppinserver.popup.usecase.PreferedPopupCommandUseCase;
import com.poppin.poppinserver.popup.usecase.TastedPopupCommandUseCase;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class ManagerInformService {
    private final ManagerInformRepository managerInformRepository;

    private final UserQueryUseCase userQueryUseCase;
    private final PosterImageCommandUseCase posterImageCommandUseCase;
    private final PreferedPopupCommandUseCase preferedPopupCommandUseCase;
    private final TastedPopupCommandUseCase tastedPopupCommandUseCase;
    private final PopupCommandUseCase popupCommandUseCase;

    @Transactional
    public ManagerInformDto createManagerInform(CreateManagerInformDto createManagerInformDto,
                                                String filteringThreeCategories,
                                                String filteringFourteenCategories,
                                                List<MultipartFile> images,
                                                Long userId) {
        User user = userQueryUseCase.findUserById(userId);

        // 프록시 카테고리 생성
        List<String> prepered = Arrays.stream(filteringThreeCategories.split(",")).toList();
        List<String> taste = Arrays.stream(filteringFourteenCategories.split(",")).toList();
        if (prepered.isEmpty() || taste.isEmpty()) {
            throw new CommonException(ErrorCode.INVALID_CATEGORY_STRING);
        }

        TastePopup tastePopup = tastedPopupCommandUseCase.createTastePopup(taste);
        PreferedPopup preferedPopup = preferedPopupCommandUseCase.createPreferedPopup(prepered);

        // 프록시 팝업 생성
        Popup popup = popupCommandUseCase.createPopup(createManagerInformDto, EOperationStatus.EXECUTING.getStatus(), tastePopup, preferedPopup);

        // 팝업 이미지 처리 및 저장
        List<PosterImage> posterImages = posterImageCommandUseCase.savePosterList(images, popup);

        // 대표사진 저장
        popupCommandUseCase.updatePopupPosterUrl(popup, posterImages.get(0));

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
                                                     String filteringThreeCategories,
                                                     String filteringFourteenCategories,
                                                     List<MultipartFile> images) {

        // 프록시 카테고리 생성
        List<String> prepered = Arrays.stream(filteringThreeCategories.split(",")).toList();
        List<String> taste = Arrays.stream(filteringFourteenCategories.split(",")).toList();
        if (prepered.isEmpty() || taste.isEmpty()) {
            throw new CommonException(ErrorCode.INVALID_CATEGORY_STRING);
        }

        TastePopup tastePopup = tastedPopupCommandUseCase.createTastePopup(taste);
        PreferedPopup preferedPopup = preferedPopupCommandUseCase.createPreferedPopup(prepered);

        // 프록시 팝업 생성
        Popup popup = popupCommandUseCase.createPopup(createManagerInformDto, EOperationStatus.EXECUTING.getStatus(), tastePopup, preferedPopup);

        // 팝업 이미지 처리 및 저장
        List<PosterImage> posterImages = posterImageCommandUseCase.savePosterList(images, popup);

        // 대표사진 저장
        popupCommandUseCase.updatePopupPosterUrl(popup, posterImages.get(0));

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
}
