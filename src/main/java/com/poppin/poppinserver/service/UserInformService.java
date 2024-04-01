package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.dto.userInform.request.CreateUserInformDto;
import com.poppin.poppinserver.dto.userInform.response.UserInformDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.PopupRepository;
import com.poppin.poppinserver.repository.PosterImageRepository;
import com.poppin.poppinserver.repository.UserRepository;
import com.poppin.poppinserver.repository.UserinformRepository;
import com.poppin.poppinserver.type.EInformProgress;
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
    private final UserinformRepository userinformRepository;
    private final PopupRepository popupRepository;
    private final PosterImageRepository posterImageRepository;
    private final UserRepository userRepository;

    private final S3Service s3Service;

    //사용자 제보 생성
    public UserInformDto createUserInform(CreateUserInformDto createUserInformDto,
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

        Popup popup = Popup.builder()
                .name(createUserInformDto.name())
                .tastePopup(tastePopup)
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

        UserInform userInform = UserInform.builder()
                .informerId(user)
                .informedAt(LocalDateTime.now())
                .popupId(popup)
                .contactLink(createUserInformDto.contactlink())
                .progress(EInformProgress.NOTEXECUTED)
                .build();

        return UserInformDto.fromEntity(userInform);

    }

}
