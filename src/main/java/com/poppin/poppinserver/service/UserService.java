package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.PreferedPopup;
import com.poppin.poppinserver.domain.TastePopup;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.domain.WhoWithPopup;
import com.poppin.poppinserver.dto.user.request.CreateUserTasteDto;
import com.poppin.poppinserver.dto.popup.response.PreferedDto;
import com.poppin.poppinserver.dto.popup.response.TasteDto;
import com.poppin.poppinserver.dto.popup.response.UserTasteDto;
import com.poppin.poppinserver.dto.popup.response.WhoWithDto;
import com.poppin.poppinserver.dto.user.request.UserInfoDto;
import com.poppin.poppinserver.dto.user.response.UserProfileDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.PreferedPopupRepository;
import com.poppin.poppinserver.repository.TastePopupRepository;
import com.poppin.poppinserver.repository.UserRepository;
import com.poppin.poppinserver.repository.WhoWithPopupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PreferedPopupRepository preferedPopupRepository;
    private final TastePopupRepository tastePopupRepository;
    private final WhoWithPopupRepository whoWithPopupRepository;
    private final S3Service s3Service;

    @Transactional
    public UserTasteDto createUserTaste(
            Long userId,
            CreateUserTasteDto createUserTasteDto
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        if (user.getPreferedPopup() != null) {
            throw new CommonException(ErrorCode.ALREADY_EXISTS_PREFERENCE);
        }
        PreferedPopup preferedPopup = PreferedPopup.builder()
                .market(createUserTasteDto.prefered().market())
                .display(createUserTasteDto.prefered().display())
                .experience(createUserTasteDto.prefered().experience())
                .wantFree(createUserTasteDto.prefered().wantFree())
                .build();
        preferedPopupRepository.save(preferedPopup);

        if (user.getTastePopup() != null) {
            throw new CommonException(ErrorCode.ALREADY_EXISTS_PREFERENCE);
        }
        TastePopup tastePopup = TastePopup.builder()
                .fasionBeauty(createUserTasteDto.taste().fasionBeauty())
                .characters(createUserTasteDto.taste().characters())
                .foodBeverage(createUserTasteDto.taste().foodBeverage())
                .webtoonAni(createUserTasteDto.taste().webtoonAni())
                .interiorThings(createUserTasteDto.taste().interiorThings())
                .movie(createUserTasteDto.taste().movie())
                .musical(createUserTasteDto.taste().musical())
                .sports(createUserTasteDto.taste().sports())
                .game(createUserTasteDto.taste().game())
                .itTech(createUserTasteDto.taste().itTech())
                .kpop(createUserTasteDto.taste().kpop())
                .alchol(createUserTasteDto.taste().alchol())
                .animalPlant(createUserTasteDto.taste().animalPlant())
                .build();
        tastePopupRepository.save(tastePopup);

        if (user.getWhoWithPopup() != null) {
            throw new CommonException(ErrorCode.ALREADY_EXISTS_PREFERENCE);
        }
        WhoWithPopup whoWithPopup = WhoWithPopup.builder()
                .solo(createUserTasteDto.whoWith().solo())
                .withFriend(createUserTasteDto.whoWith().withFriend())
                .withFamily(createUserTasteDto.whoWith().withFamily())
                .withLover(createUserTasteDto.whoWith().withLover())
                .build();
        whoWithPopupRepository.save(whoWithPopup);

        user.updatePopupTaste(preferedPopup, tastePopup, whoWithPopup);
        userRepository.save(user);

        return UserTasteDto.builder()
                .preferedDto(PreferedDto.fromEntity(preferedPopup))
                .tasteDto(TasteDto.fromEntity(tastePopup))
                .whoWithDto(WhoWithDto.fromEntity(whoWithPopup))
                .build();
    }

    @Transactional
    public UserTasteDto readUserTaste(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        return UserTasteDto.builder()
                .preferedDto(PreferedDto.fromEntity(user.getPreferedPopup()))
                .tasteDto(TasteDto.fromEntity(user.getTastePopup()))
                .whoWithDto(WhoWithDto.fromEntity(user.getWhoWithPopup()))
                .build();
    }

    @Transactional
    public UserTasteDto updateUserTaste(Long userId, CreateUserTasteDto createUserTasteDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        PreferedPopup preferedPopup = user.getPreferedPopup();
        preferedPopup.update(createUserTasteDto.prefered().market(),
                createUserTasteDto.prefered().display(),
                createUserTasteDto.prefered().experience(),
                createUserTasteDto.prefered().wantFree());
        preferedPopupRepository.save(preferedPopup);

        TastePopup tastePopup = user.getTastePopup();
        tastePopup.update(createUserTasteDto.taste().fasionBeauty(),
                createUserTasteDto.taste().characters(),
                createUserTasteDto.taste().foodBeverage(),
                createUserTasteDto.taste().webtoonAni(),
                createUserTasteDto.taste().interiorThings(),
                createUserTasteDto.taste().movie(),
                createUserTasteDto.taste().musical(),
                createUserTasteDto.taste().sports(),
                createUserTasteDto.taste().game(),
                createUserTasteDto.taste().itTech(),
                createUserTasteDto.taste().kpop(),
                createUserTasteDto.taste().alchol(),
                createUserTasteDto.taste().animalPlant());
        tastePopupRepository.save(tastePopup);

        WhoWithPopup whoWithPopup = user.getWhoWithPopup();
        whoWithPopup.update(createUserTasteDto.whoWith().solo(),
                createUserTasteDto.whoWith().withFriend(),
                createUserTasteDto.whoWith().withFamily(),
                createUserTasteDto.whoWith().withLover());
        whoWithPopupRepository.save(whoWithPopup);

        user.updatePopupTaste(preferedPopup, tastePopup, whoWithPopup);
        userRepository.save(user);

        return UserTasteDto.builder()
                .preferedDto(PreferedDto.fromEntity(preferedPopup))
                .tasteDto(TasteDto.fromEntity(tastePopup))
                .whoWithDto(WhoWithDto.fromEntity(whoWithPopup))
                .build();
    }

    public UserProfileDto readUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        return UserProfileDto.builder()
                .email(user.getEmail())
                .userImageUrl(user.getProfileImageUrl())
                .nickname(user.getNickname())
                .birthDate(user.getBirthDate())
                .provider(user.getProvider())
                .build();
    }

    public String updateProfileImage(Long userId, MultipartFile profileImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        String profileImageUrl = s3Service.uploadUserProfile(profileImage, userId);
        user.updateProfileImage(profileImageUrl);
        userRepository.save(user);
        return user.getProfileImageUrl();
    }

    public void deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        user.deleteProfileImage();
        userRepository.save(user);
    }

    public UserProfileDto updateUserNicknameAndBirthDate(Long userId, UserInfoDto userInfoDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        if (userRepository.findByNickname(userInfoDto.nickname()).isPresent() && (userId != user.getId())) {
            throw new CommonException(ErrorCode.DUPLICATED_NICKNAME);
        }
        user.updateUserNicknameAndBirthDate(userInfoDto.nickname(), userInfoDto.birthDate());
        userRepository.save(user);

        return UserProfileDto.builder()
                .nickname(user.getNickname())
                .birthDate(user.getBirthDate())
                .build();
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        user.softDelete();
        userRepository.save(user);
    }
}
