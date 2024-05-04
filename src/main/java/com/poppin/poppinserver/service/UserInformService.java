package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.dto.userInform.request.CreateUserInformDto;
import com.poppin.poppinserver.dto.userInform.request.UpdateUserInfromDto;
import com.poppin.poppinserver.dto.userInform.response.UserInformDto;
import com.poppin.poppinserver.dto.userInform.response.UserInformSummaryDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.*;
import com.poppin.poppinserver.type.EInformProgress;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                .alchol(createTasteDto.alcohol())
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
                .operationStatus("EXECUTING")
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
                                          List<MultipartFile> images,
                                          Long adminId){
        UserInform userInform = userInformRepository.findById(updateUserInfromDto.userInformId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER_INFORM));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        CreateTasteDto createTasteDto = updateUserInfromDto.taste();
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
        for(String url : newUrls){
            PosterImage posterImage = PosterImage.builder()
                    .posterUrl(url)
                    .popup(popup)
                    .build();
            posterImages.add(posterImage);
        }
        posterImageRepository.saveAll(posterImages);
        popup.updatePosterUrl(newUrls.get(0));

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
                "EXECUTING",
                admin
        );

        popup = popupRepository.save(popup);

        userInform.update(EInformProgress.EXECUTING, admin);
        userInform = userInformRepository.save(userInform);

        return UserInformDto.fromEntity(userInform);
    }

    @Transactional
    public UserInformDto uploadPopup(UpdateUserInfromDto updateUserInfromDto,
                                          List<MultipartFile> images,
                                          Long adminId){
        UserInform userInform = userInformRepository.findById(updateUserInfromDto.userInformId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER_INFORM));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        CreateTasteDto createTasteDto = updateUserInfromDto.taste();
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

        //날짜 요청 유효성 검증
        if (updateUserInfromDto.openDate().isAfter(updateUserInfromDto.closeDate())) {
            throw new CommonException(ErrorCode.INVALID_DATE_PARAMETER);
        }

        //현재 운영상태 정의
        String operationStatus;
        if (updateUserInfromDto.openDate().isAfter(LocalDate.now())){
            Period period = Period.between(LocalDate.now(), updateUserInfromDto.openDate());
            operationStatus = "D-" + period.getDays();
        } else if (updateUserInfromDto.closeDate().isBefore(LocalDate.now())) {
            operationStatus = "TERMINATED";
        }
        else{
            operationStatus = "OPERATING";
        }

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
                operationStatus,
                admin
        );

        userInform.update(EInformProgress.EXECUTED, admin);
        userInform = userInformRepository.save(userInform);

        return UserInformDto.fromEntity(userInform);
    }

    @Transactional
    public List<UserInformSummaryDto> reatUserInformList(){
        List<UserInform> userInforms = userInformRepository.findAll();

        return UserInformSummaryDto.fromEntityList(userInforms);
    }
}
