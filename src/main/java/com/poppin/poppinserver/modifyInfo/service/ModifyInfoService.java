package com.poppin.poppinserver.modifyInfo.service;

import com.poppin.poppinserver.alarm.domain.PopupAlarmKeyword;
import com.poppin.poppinserver.alarm.repository.PopupAlarmKeywordRepository;
import com.poppin.poppinserver.core.type.EOperationStatus;
import com.poppin.poppinserver.inform.repository.ModifyInformRepository;
import com.poppin.poppinserver.modifyInfo.domain.ModifyImages;
import com.poppin.poppinserver.modifyInfo.domain.ModifyInfo;
import com.poppin.poppinserver.modifyInfo.dto.request.CreateModifyInfoDto;
import com.poppin.poppinserver.modifyInfo.dto.response.ModifyInfoDto;
import com.poppin.poppinserver.modifyInfo.repository.ModifyImageReposiroty;
import com.poppin.poppinserver.modifyInfo.usecase.ModifyImagesCommandUseCase;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.popup.domain.PreferedPopup;
import com.poppin.poppinserver.popup.domain.TastePopup;
import com.poppin.poppinserver.popup.service.S3Service;
import com.poppin.poppinserver.popup.usecase.*;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModifyInfoService {
    private final ModifyInformRepository modifyInformRepository;
    private final PopupAlarmKeywordRepository popupAlarmKeywordRepository;

    private final S3Service s3Service;

    private final UserQueryUseCase userQueryUseCase;
    private final TastedPopupCommandUseCase tastedPopupCommandUseCase;
    private final PreferedPopupCommandUseCase preferedPopupCommandUseCase;
    private final PosterImageCommandUseCase posterImageCommandUseCase;
    private final PopupQueryUseCase popupQueryUseCase;
    private final PopupCommandUseCase popupCommandUseCase;
    private final ModifyImagesCommandUseCase modifyImagesCommandUseCase;

    @Transactional
    public ModifyInfoDto createModifyInfo(CreateModifyInfoDto createModifyInfoDto,
                                          List<MultipartFile> images,
                                          Long userId) {
        Long popupId = Long.valueOf(createModifyInfoDto.popupId());

        User user = userQueryUseCase.findUserById(userId);
        Popup popup = popupQueryUseCase.findPopupById(popupId);

        // 프록시 팝업 생성
        PreferedPopup proxyPrefered = preferedPopupCommandUseCase.createProxyPreferedPopup(popup.getPreferedPopup());

        TastePopup proxyTaste = tastedPopupCommandUseCase.createProxyTastePopup(popup.getTastePopup());

        Popup proxyPopup = popupCommandUseCase.copyPopup(popup, proxyPrefered, proxyTaste, EOperationStatus.EXECUTING.getStatus());

        // 프록시 이미지 복제 및 생성
        List<PosterImage> proxyImages = posterImageCommandUseCase.copyPosterList(popup, proxyPopup);

        // 대표사진 저장
        popupCommandUseCase.updatePopupPosterUrl(proxyPopup, proxyImages.get(0));

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

        List<String> fileUrls = modifyImagesCommandUseCase.saveModifyImagerList(images, modifyInfo);

        return ModifyInfoDto.fromEntity(modifyInfo, fileUrls);
    } // 사용자 정보수정요청 생성

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
            modifyImagesCommandUseCase.deleteModifyImageList(modifyInfo);

            // modify info 삭제
            modifyInformRepository.delete(modifyInfo);

            // proxy popup 이미지 삭제
            posterImageCommandUseCase.deletePosterList(proxyPopup);

            // proxy popup 알람 키워드 삭제
            popupAlarmKeywordRepository.deleteAllByPopupId(proxyPopup);

            // proxy popup 삭제
            log.info("delete proxy popup");
            popupCommandUseCase.deletePopup(proxyPopup);
        }
    } // 프록시 팝업 삭제 및 정보수정요청 삭제
}
