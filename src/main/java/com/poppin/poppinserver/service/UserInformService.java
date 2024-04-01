package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.dto.userInform.request.CreateUserInformDto;
import com.poppin.poppinserver.dto.userInform.request.UpdateUserInfromDto;
import com.poppin.poppinserver.dto.userInform.response.UserInformDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.*;
import com.poppin.poppinserver.type.EInformProgress;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserInformService {
    private final UserInformRepository userInformRepository;
    private final PopupRepository popupRepository;
    private final TastePopupRepository tastePopupRepository;
    private final PosterImageRepository posterImageRepository;
    private final UserRepository userRepository;
    private final AlarmKeywordRepository alarmKeywordRepository;
    private final PreferedPopupRepository preferedPopupRepository;

    private final S3Service s3Service;

    @Transactional
    public UserInformDto createUserInform(CreateUserInformDto createUserInformDto, //사용자 제보 생성
                                          List<MultipartFile> images,
                                          Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        CreateTasteDto createTasteDto = createUserInformDto.taste();
        TastePopup tastePopup = TastePopup.builder()
                .fasionBeauty(createTasteDto.fasionBeauty())
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
                .alchol(createTasteDto.alchol())
                .animalPlant(createTasteDto.animalPlant())
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
                .name(createUserInformDto.name())
                .tastePopup(tastePopup)
                .preferedPopup(preferedPopup)
                .build();
        popup = popupRepository.save(popup);
        log.info(popup.toString());

        // 팝업 이미지 처리 및 저장
        List<String> fileUrls = s3Service.uploadPopupPoster(images, popup.getId());

        List<PosterImage> posterImages = new ArrayList<>();
        for(String url : fileUrls){
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
                .informedAt(LocalDateTime.now())
                .popupId(popup)
                .contactLink(createUserInformDto.contactLink())
                .progress(EInformProgress.NOTEXECUTED)
                .build();
        userInform = userInformRepository.save(userInform);

        return UserInformDto.fromEntity(userInform);
    }

    @Transactional
    public UserInformDto readUserInform(Long userInformId){ // 사용자 제보 조회
        UserInform userInform = userInformRepository.findById(userInformId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER_INFORM));

        return UserInformDto.fromEntity(userInform);
    }

    @Transactional
    public UserInformDto updateUserInform(UpdateUserInfromDto updateUserInfromDto,
                                          List<MultipartFile> images
                                          ){
        UserInform userInform = userInformRepository.findById(updateUserInfromDto.userInformId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER_INFORM));

        User admin = userRepository.findById(updateUserInfromDto.adminId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        CreateTasteDto createTasteDto = updateUserInfromDto.taste();
        TastePopup tastePopup = userInform.getPopupId().getTastePopup();
        tastePopup.update(createTasteDto.fasionBeauty(),
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
                createTasteDto.alchol(),
                createTasteDto.animalPlant());
        tastePopupRepository.save(tastePopup);

        CreatePreferedDto createPreferedDto = updateUserInfromDto.prefered();
        PreferedPopup preferedPopup = userInform.getPopupId().getPreferedPopup();
        preferedPopup.update(createPreferedDto.market(),
                createPreferedDto.display(),
                createPreferedDto.experience(),
                createPreferedDto.wantFree());
        preferedPopupRepository.save(preferedPopup);

        Popup popup = userInform.getPopupId();

        // 팝업 이미지 처리 및 저장
        List<String> fileUrls = s3Service.uploadPopupPoster(images, popup.getId());

        List<PosterImage> posterImages = new ArrayList<>();
        for(String url : fileUrls){
            PosterImage posterImage = PosterImage.builder()
                    .posterUrl(url)
                    .popup(popup)
                    .build();
            posterImages.add(posterImage);
        }
        posterImageRepository.saveAll(posterImages);
        popup.updatePosterUrl(fileUrls.get(0));

        // 기존 키워드 삭제 및 다시 저장
        alarmKeywordRepository.deleteAll(popup.getAlarmKeywords());

        List<AlarmKeyword> alarmKeywords = new ArrayList<>();
        for(String keyword : updateUserInfromDto.keywords()){
            alarmKeywords.add(AlarmKeyword.builder()
                    .popupId(popup)
                    .keyword(keyword)
                    .build());
        }
        alarmKeywordRepository.saveAll(alarmKeywords);

        popup.update(
                updateUserInfromDto.homepageLink(),
                updateUserInfromDto.name(),
                updateUserInfromDto.introduce(),
                updateUserInfromDto.address(),
                updateUserInfromDto.addressDetail(),
                updateUserInfromDto.entranceFee(),
                updateUserInfromDto.resvRequired(),
                updateUserInfromDto.availableAge(),
                updateUserInfromDto.parkingAvailable(),
                updateUserInfromDto.openDate(),
                updateUserInfromDto.closeDate(),
                updateUserInfromDto.openTime(),
                updateUserInfromDto.closeTime(),
                updateUserInfromDto.operationExcept(),
                updateUserInfromDto.operationExcept()
        );

        popup = popupRepository.save(popup);

        userInform.update(EInformProgress.EXECUTING, admin);
        userInform = userInformRepository.save(userInform);

        return UserInformDto.fromEntity(userInform);
    }
}
