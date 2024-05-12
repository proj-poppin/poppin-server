package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.faq.response.FaqResponseDto;
import com.poppin.poppinserver.dto.popup.response.*;
import com.poppin.poppinserver.dto.review.response.ReviewFinishDto;
import com.poppin.poppinserver.dto.review.response.ReviewUncertiDto;
import com.poppin.poppinserver.dto.review.response.ReviewCertiDto;
import com.poppin.poppinserver.dto.user.request.CreateUserTasteDto;
import com.poppin.poppinserver.dto.user.request.UserInfoDto;
import com.poppin.poppinserver.dto.user.response.NicknameDto;
import com.poppin.poppinserver.dto.user.response.UserProfileDto;
import com.poppin.poppinserver.dto.visitorData.response.VisitorDataRvDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.*;
import com.poppin.poppinserver.util.RandomNicknameUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PreferedPopupRepository preferedPopupRepository;
    private final TastePopupRepository tastePopupRepository;
    private final WhoWithPopupRepository whoWithPopupRepository;
    private final ReviewRepository reviewRepository;
    private final PopupRepository popupRepository;
    private final VisitRepository visitRepository;
    private final VisitorDataRepository visitorDataRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final S3Service s3Service;
    private final FreqQuestionRepository freqQuestionRepository;

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
                .market(createUserTasteDto.preference().market())
                .display(createUserTasteDto.preference().display())
                .experience(createUserTasteDto.preference().experience())
                .wantFree(createUserTasteDto.preference().wantFree())
                .build();
        preferedPopupRepository.save(preferedPopup);

        if (user.getTastePopup() != null) {
            throw new CommonException(ErrorCode.ALREADY_EXISTS_PREFERENCE);
        }
        TastePopup tastePopup = TastePopup.builder()
                .fasionBeauty(createUserTasteDto.taste().fashionBeauty())
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
                .alchol(createUserTasteDto.taste().alcohol())
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
                .preference(PreferedDto.fromEntity(preferedPopup))
                .taste(TasteDto.fromEntity(tastePopup))
                .whoWith(WhoWithDto.fromEntity(whoWithPopup))
                .build();
    }

    @Transactional
    public UserTasteDto readUserTaste(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        return UserTasteDto.builder()
                .preference(PreferedDto.fromEntity(user.getPreferedPopup()))
                .taste(TasteDto.fromEntity(user.getTastePopup()))
                .whoWith(WhoWithDto.fromEntity(user.getWhoWithPopup()))
                .build();
    }

    @Transactional
    public UserTasteDto updateUserTaste(Long userId, CreateUserTasteDto createUserTasteDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        PreferedPopup preferedPopup = user.getPreferedPopup();
        preferedPopup.update(createUserTasteDto.preference().market(),
                createUserTasteDto.preference().display(),
                createUserTasteDto.preference().experience(),
                createUserTasteDto.preference().wantFree());
        preferedPopupRepository.save(preferedPopup);

        TastePopup tastePopup = user.getTastePopup();
        tastePopup.update(createUserTasteDto.taste().fashionBeauty(),
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
                createUserTasteDto.taste().alcohol(),
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
                .preference(PreferedDto.fromEntity(preferedPopup))
                .taste(TasteDto.fromEntity(tastePopup))
                .whoWith(WhoWithDto.fromEntity(whoWithPopup))
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

    public String createProfileImage(Long userId, MultipartFile profileImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        String profileImageUrl = s3Service.uploadUserProfile(profileImage, userId);
        user.updateProfileImage(profileImageUrl);
        userRepository.save(user);

        return user.getProfileImageUrl();
    }

    public String updateProfileImage(Long userId, MultipartFile profileImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        String profileImageUrl = s3Service.replaceImage(user.getProfileImageUrl(), profileImage, userId);
        user.updateProfileImage(profileImageUrl);
        userRepository.save(user);

        return user.getProfileImageUrl();
    }

    public void deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        s3Service.deleteImage(user.getProfileImageUrl());
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

    /*작성완료 후기 조회*/
    public List<ReviewFinishDto> getFinishReviewList(Long userId){

        List<ReviewFinishDto> reviewFinishDtoList = new ArrayList<>();
        List<Review> reviewList = reviewRepository.findByUserId(userId);

        for (Review review : reviewList){
            Popup popup = popupRepository.findByReviewId(review.getPopup().getId());
            ReviewFinishDto reviewFinishDto = ReviewFinishDto.fromEntity(review.getId(), popup.getId(), popup.getName(), review.getIsCertificated(),review.getCreatedAt());
            reviewFinishDtoList.add(reviewFinishDto);
        }
        return reviewFinishDtoList;
    }

    /*마이페이지 인증 후기 보기*/
    public ReviewCertiDto getCertifiedReview(Long userId, Long reviewId, Long popupId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP)); /*여기서 인증된 후기의 popupId로 조회한다*/

        Review review = reviewRepository.findByIdAndPopupId(reviewId, popupId); /* 후기 */
        if (review == null) throw new CommonException(ErrorCode.NOT_FOUND_REVIEW);

        Visit visit = visitRepository.findByUserIdAndPopupId(userId, popupId);
        if (visit == null) throw new CommonException(ErrorCode.NOT_FOUND_REALTIMEVISIT); /*인증 테이블에 값이 없으면 익셉션 처리*/

        VisitorData visitorData = visitorDataRepository.findByReviewIdAndPopupId(reviewId,popupId);
        VisitorDataRvDto visitorDataRvDto = VisitorDataRvDto.fromEntity(visitorData.getVisitDate(),visitorData.getSatisfaction(),visitorData.getCongestion());

        List<String> reviewImageListUrl = reviewImageRepository.findUrlAllByReviewId(reviewId); /*url을 모두 받기*/

        return ReviewCertiDto.fromEntity(
                popup.getName(),
                popup.getPosterUrl(),
                review.getIsCertificated(),
                user.getNickname(),
                visit.getCreatedAt(),
                review.getCreatedAt(),
                visitorDataRvDto,
                review.getText(),
                reviewImageListUrl
        );
    }

    /*마이페이지 인증 후기 보기*/
    public ReviewUncertiDto getUncertifiedReview(Long userId, Long reviewId, Long popupId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP)); /*여기서 인증된 후기의 popupId로 조회한다*/

        Review review = reviewRepository.findByIdAndPopupId(reviewId, popupId); /* 후기 */
        if (review == null )throw new CommonException(ErrorCode.NOT_FOUND_REVIEW);

        VisitorData visitorData = visitorDataRepository.findByReviewIdAndPopupId(reviewId,popupId);
        VisitorDataRvDto visitorDataRvDto = VisitorDataRvDto.fromEntity(visitorData.getVisitDate(),visitorData.getSatisfaction(),visitorData.getCongestion());

        List<String> reviewImageListUrl = reviewImageRepository.findUrlAllByReviewId(reviewId); /*url을 모두 받기*/

        return ReviewUncertiDto.fromEntity(
                popup.getName(),
                popup.getPosterUrl(),
                review.getIsCertificated(),
                user.getNickname(),
                review.getCreatedAt(),
                visitorDataRvDto,
                review.getText(),
                reviewImageListUrl
        );
    }

    public List<PopupCertiDto> getCertifiedPopupList(Long userId){
        /* 1. userId로 visit 리스트 뽑기
         *  2. visit 리스트의 popupid 와 popup의 id 일치하는 popup 뽑기
         */
        List<Visit> visitList = visitRepository.findAllByUserId(userId);
        if (visitList.isEmpty())throw new CommonException(ErrorCode.NOT_FOUND_VISIT);

        List<PopupCertiDto> popupCertiDtoList = new ArrayList<>();

        for (Visit visit : visitList){
            Long vdPopupId = visit.getPopup().getId();
            Popup popup = popupRepository.findTopByPopupId(vdPopupId);
            PopupCertiDto popupCertiDto = PopupCertiDto.fromEntity(popup.getName(),popup.getPosterUrl(),visit.getCreatedAt());
            popupCertiDtoList.add(popupCertiDto);
        }
        return popupCertiDtoList;
    }

    public List<FaqResponseDto> readFAQs() {
        List<FreqQuestion> freqQuestionList = freqQuestionRepository.findAllByOrderByCreatedAtDesc();
        List<FaqResponseDto> faqDtoList = new ArrayList<>();
        for (FreqQuestion freqQuestion : freqQuestionList) {
            faqDtoList.add(FaqResponseDto.builder()
                    .id(freqQuestion.getId())
                    .question(freqQuestion.getQuestion())
                    .answer(freqQuestion.getAnswer())
                    .createdAt(freqQuestion.getCreatedAt().toString())
                    .build());
        }
        return faqDtoList;
    }

    public NicknameDto generateRandomNickname() {
        String randomNickname = RandomNicknameUtil.generateRandomNickname();
        return new NicknameDto(randomNickname);
    }

    @Transactional
    public void deleteAllRelatedInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        s3Service.deleteImage(user.getProfileImageUrl());   // 유저 프로필 이미지 S3에서도 삭제
        reviewRepository.deleteAllByUserId(userId);     // 유저가 남긴 모든 후기 삭제
    }
}
