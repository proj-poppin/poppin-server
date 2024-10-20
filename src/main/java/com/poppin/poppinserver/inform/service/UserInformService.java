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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.poppin.poppinserver.user.usecase.ReadUserUseCase;
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
public class UserInformService {
    private final UserInformRepository userInformRepository;
    private final PopupRepository popupRepository;
    private final TastePopupRepository tastePopupRepository;
    private final PosterImageRepository posterImageRepository;
    private final PreferedPopupRepository preferedPopupRepository;

    private final S3Service s3Service;

    private final ReadUserUseCase readUserUseCase;

    @Transactional
    public UserInformDto createUserInform(String name, String contactLink, Boolean fashionBeauty, Boolean characters,
                                          Boolean foodBeverage, Boolean webtoonAni, Boolean interiorThings,
                                          Boolean movie, Boolean musical, Boolean sports, Boolean game, Boolean itTech,
                                          Boolean kpop, Boolean alcohol, Boolean animalPlant, Boolean etc,
                                          List<MultipartFile> images, Long userId) {
        User user = readUserUseCase.findUserById(userId);

        TastePopup tastePopup = TastePopup.builder()
                .fasionBeauty(fashionBeauty)
                .characters(characters)
                .foodBeverage(foodBeverage)
                .webtoonAni(webtoonAni)
                .interiorThings(interiorThings)
                .movie(movie)
                .musical(musical)
                .sports(sports)
                .game(game)
                .itTech(itTech)
                .kpop(kpop)
                .alcohol(alcohol)
                .animalPlant(animalPlant)
                .etc(etc)
                .build();
        tastePopupRepository.save(tastePopup);

        PreferedPopup preferedPopup = PreferedPopup.builder()
                .wantFree(false)
                .market(false)
                .experience(false)
                .display(false)
                .build();
        preferedPopupRepository.save(preferedPopup);

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
    public UserInformDto createGuestUserInform(String name, String contactLink, Boolean fashionBeauty,
                                               Boolean characters,
                                               Boolean foodBeverage, Boolean webtoonAni, Boolean interiorThings,
                                               Boolean movie, Boolean musical, Boolean sports, Boolean game,
                                               Boolean itTech,
                                               Boolean kpop, Boolean alcohol, Boolean animalPlant, Boolean etc,
                                               List<MultipartFile> images) {

        TastePopup tastePopup = TastePopup.builder()
                .fasionBeauty(fashionBeauty)
                .characters(characters)
                .foodBeverage(foodBeverage)
                .webtoonAni(webtoonAni)
                .interiorThings(interiorThings)
                .movie(movie)
                .musical(musical)
                .sports(sports)
                .game(game)
                .itTech(itTech)
                .kpop(kpop)
                .alcohol(alcohol)
                .animalPlant(animalPlant)
                .etc(etc)
                .build();
        tastePopupRepository.save(tastePopup);

        PreferedPopup preferedPopup = PreferedPopup.builder()
                .wantFree(false)
                .market(false)
                .experience(false)
                .display(false)
                .build();
        preferedPopupRepository.save(preferedPopup);

        Popup popup = Popup.builder()
                .name(name)
                .tastePopup(tastePopup)
                .preferedPopup(preferedPopup)
                .operationStatus(EOperationStatus.EXECUTING.getStatus())
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
