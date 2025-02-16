package com.poppin.poppinserver.inform.service;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.EInformProgress;
import com.poppin.poppinserver.core.type.EOperationStatus;
import com.poppin.poppinserver.inform.domain.UserInform;
import com.poppin.poppinserver.inform.dto.userInform.response.UserInformDto;
import com.poppin.poppinserver.inform.repository.UserInformRepository;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.popup.domain.PreferedPopup;
import com.poppin.poppinserver.popup.domain.TastePopup;
import com.poppin.poppinserver.popup.repository.PopupRepository;
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
public class UserInformService {
    private final UserInformRepository userInformRepository;

    private final PopupRepository popupRepository;

    private final UserQueryUseCase userQueryUseCase;
    private final PosterImageCommandUseCase posterImageCommandUseCase;
    private final PreferedPopupCommandUseCase preferedPopupCommandUseCase;
    private final TastedPopupCommandUseCase tastedPopupCommandUseCase;

    @Transactional
    public UserInformDto createUserInform(String name, String contactLink, String filteringFourteenCategories,
                                          List<MultipartFile> images, Long userId) {
        User user = userQueryUseCase.findUserById(userId);

        // 프록시 카테고리 생성
        List<String> taste = Arrays.stream(filteringFourteenCategories.split(",")).toList();
        if (taste.isEmpty()) {
            throw new CommonException(ErrorCode.INVALID_CATEGORY_STRING);
        }

        TastePopup tastePopup = tastedPopupCommandUseCase.createTastePopup(taste);

        PreferedPopup preferedPopup = preferedPopupCommandUseCase.createEmptyPreferedPopup();

        // 프록시 팝업 생성
        Popup popup = Popup.builder()
                .name(name)
                .tastePopup(tastePopup)
                .preferedPopup(preferedPopup)
                .entranceRequired(true)
                .operationStatus(EOperationStatus.EXECUTING.getStatus())
                .build();
        popup = popupRepository.save(popup);
        log.info(popup.toString());

        // 팝업 이미지 처리 및 저장
        List<PosterImage> posterImages = posterImageCommandUseCase.savePosterList(images, popup);
        popup.updatePosterUrl(posterImages.get(0).getPosterUrl());

        popup = popupRepository.save(popup);

        UserInform userInform = UserInform.builder()
                .informerId(user)
                .popupId(popup)
                .contactLink(contactLink)
                .progress(EInformProgress.NOTEXECUTED)
                .build();
        userInform = userInformRepository.save(userInform);

        return UserInformDto.fromEntity(userInform);
    } // 제보 생성

    @Transactional
    public UserInformDto createGuestUserInform(String name, String contactLink, String filteringFourteenCategories,
                                               List<MultipartFile> images) {

        log.info("createUserInform");
        log.info("filteringFourteenCategories: {}", filteringFourteenCategories);

        // 프록시 카테고리 생성
        List<String> taste = Arrays.stream(filteringFourteenCategories.split(",")).toList();
        if (taste.isEmpty()) {
            throw new CommonException(ErrorCode.INVALID_CATEGORY_STRING);
        }

        TastePopup tastePopup = tastedPopupCommandUseCase.createTastePopup(taste);

        PreferedPopup preferedPopup = preferedPopupCommandUseCase.createEmptyPreferedPopup();

        // 프록시 팝업 생성
        Popup popup = Popup.builder()
                .name(name)
                .tastePopup(tastePopup)
                .preferedPopup(preferedPopup)
                .operationStatus(EOperationStatus.EXECUTING.getStatus())
                .build();
        popup = popupRepository.save(popup);
        log.info(popup.toString());

        // 팝업 이미지 처리 및 저장
        List<PosterImage> posterImages = posterImageCommandUseCase.savePosterList(images, popup);
        popup.updatePosterUrl(posterImages.get(0).getPosterUrl());

        popup = popupRepository.save(popup);

        UserInform userInform = UserInform.builder()
                .informerId(null)
                .popupId(popup)
                .contactLink(contactLink)
                .progress(EInformProgress.NOTEXECUTED)
                .build();
        userInform = userInformRepository.save(userInform);

        return UserInformDto.fromEntity(userInform);
    } // 제보 생성
}
