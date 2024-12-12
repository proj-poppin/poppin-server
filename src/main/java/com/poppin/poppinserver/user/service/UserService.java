package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.util.RandomNicknameUtil;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.popup.repository.PosterImageRepository;
import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.review.dto.response.ReviewCertiDto;
import com.poppin.poppinserver.review.dto.response.ReviewFinishDto;
import com.poppin.poppinserver.review.dto.response.ReviewUncertiDto;
import com.poppin.poppinserver.review.repository.ReviewQueryRepository;
import com.poppin.poppinserver.review.usecase.ReviewImageQueryUseCase;
import com.poppin.poppinserver.user.domain.FreqQuestion;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.dto.faq.response.UserFaqResponseDto;
import com.poppin.poppinserver.user.dto.user.request.UpdateUserInfoDto;
import com.poppin.poppinserver.user.dto.user.response.UserMypageDto;
import com.poppin.poppinserver.user.dto.user.response.UserNicknameResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserProfileDto;
import com.poppin.poppinserver.user.repository.FreqQuestionRepository;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import com.poppin.poppinserver.visit.domain.Visit;
import com.poppin.poppinserver.visit.domain.VisitorData;
import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataRvDto;
import com.poppin.poppinserver.visit.repository.VisitRepository;
import com.poppin.poppinserver.visit.repository.VisitorDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserQueryUseCase userQueryUseCase;
    private final UserQueryRepository userQueryRepository;
    private final ReviewQueryRepository reviewRepository;
    private final PopupRepository popupRepository;
    private final VisitRepository visitRepository;
    private final VisitorDataRepository visitorDataRepository;
    private final ReviewImageQueryUseCase reviewImageQueryUseCase;
    private final FreqQuestionRepository freqQuestionRepository;
    private final PosterImageRepository posterImageRepository;


    // TODO: 삭제 예정
    public UserMypageDto readUser(Long userId) {
        User user = userQueryUseCase.findUserById(userId);

        return UserMypageDto.builder()
                .userImageUrl(user.getProfileImageUrl())
                .nickname(user.getNickname())
                .writtenReview(user.getReviewCnt())
                .visitedPopupCnt(user.getVisitedPopupCnt())
                .build();
    }

    // TODO: 삭제 예정
    public UserProfileDto readUserProfile(Long userId) {
        User user = userQueryUseCase.findUserById(userId);

        return UserProfileDto.builder()
                .email(user.getEmail())
                .userImageUrl(user.getProfileImageUrl())
                .nickname(user.getNickname())
                .provider(user.getProvider())
                .build();
    }

    public UserProfileDto updateUserNickname(Long userId, UpdateUserInfoDto updateUserInfoDto) {
        User user = userQueryUseCase.findUserById(userId);
        if (userQueryRepository.findByNickname(updateUserInfoDto.nickname()).isPresent() && (userId != user.getId())) {
            throw new CommonException(ErrorCode.DUPLICATED_NICKNAME);
        }
        user.updateUserNickname(updateUserInfoDto.nickname());
        userQueryRepository.save(user);

        return UserProfileDto.builder()
                .provider(user.getProvider())
                .userImageUrl(user.getProfileImageUrl())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    /*마이페이지 - 작성완료 후기 조회*/
    public List<ReviewFinishDto> getFinishReviewList(Long userId) {

        List<ReviewFinishDto> reviewFinishDtoList = new ArrayList<>();
        List<Review> reviewList = reviewRepository.findByUserId(userId);

        for (Review review : reviewList) {
            // 팝업 정보
            Popup popup = popupRepository.findByReviewId(review.getPopup().getId());

            // 팝업 이미지 정보
            List<PosterImage> posterImages = posterImageRepository.findAllByPopupId(popup);
            List<String> imageList = new ArrayList<>();
            if (!posterImages.isEmpty()) {
                for (PosterImage posterImage : posterImages) {
                    imageList.add(posterImage.getPosterUrl());
                }
            } else {
                imageList.add(null);
            }

            ReviewFinishDto reviewFinishDto = ReviewFinishDto.fromEntity(review.getId(), popup.getId(), popup.getName(),
                    review.getIsCertificated(), review.getCreatedAt(), imageList);
            reviewFinishDtoList.add(reviewFinishDto);
        }
        return reviewFinishDtoList;
    }

    /*마이페이지 - 작성 완료 후기 조회 - 인증 후기 보기*/
    public ReviewCertiDto getCertifiedReview(Long userId, Long reviewId, Long popupId) {
        User user = userQueryRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP)); /*여기서 인증된 후기의 popupId로 조회한다*/

        Review review = reviewRepository.findByIdAndPopupId(reviewId, popupId); /* 후기 */
        if (review == null) {
            throw new CommonException(ErrorCode.NOT_FOUND_REVIEW);
        }

        Visit visit = visitRepository.findByUserIdAndPopupId(userId, popupId);
        if (visit == null) {
            throw new CommonException(ErrorCode.NOT_FOUND_REALTIMEVISIT); /*인증 테이블에 값이 없으면 익셉션 처리*/
        }

        VisitorData visitorData = visitorDataRepository.findByReviewIdAndPopupId(reviewId, popupId);
        VisitorDataRvDto visitorDataRvDto = VisitorDataRvDto.fromEntity(visitorData.getVisitDate(),
                visitorData.getSatisfaction(), visitorData.getCongestion());

        List<String> reviewImageListUrl = reviewImageQueryUseCase.findUrlAllByReviewId(reviewId); /*url을 모두 받기*/

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

    /*마이페이지 - 작성완료 후기 조회 - 일반 후기 보기*/
    public ReviewUncertiDto getUncertifiedReview(Long userId, Long reviewId, Long popupId) {
        User user = userQueryRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP)); /*여기서 인증된 후기의 popupId로 조회한다*/

        Review review = reviewRepository.findByIdAndPopupId(reviewId, popupId); /* 후기 */
        if (review == null) {
            throw new CommonException(ErrorCode.NOT_FOUND_REVIEW);
        }

        VisitorData visitorData = visitorDataRepository.findByReviewIdAndPopupId(reviewId, popupId);
        VisitorDataRvDto visitorDataRvDto = VisitorDataRvDto.fromEntity(visitorData.getVisitDate(),
                visitorData.getSatisfaction(), visitorData.getCongestion());

        List<String> reviewImageListUrl = reviewImageQueryUseCase.findUrlAllByReviewId(reviewId); /*url을 모두 받기*/

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

    //TODO: 삭제 예정
//    public List<PopupCertiDto> getCertifiedPopupList(Long userId) {
//        /* 1. userId로 visit 리스트 뽑기
//         *  2. visit 리스트의 popupid 와 popup의 id 일치하는 popup 뽑기
//         */
//        List<Visit> visitList = visitRepository.findAllByUserId(userId);
//        if (visitList.isEmpty()) {
//            throw new CommonException(ErrorCode.NOT_FOUND_VISIT);
//        }
//
//        List<PopupCertiDto> popupCertiDtoList = new ArrayList<>();
//
//        for (Visit visit : visitList) {
//            Long vdPopupId = visit.getPopup().getId();
//            Popup popup = popupRepository.findTopByPopupId(vdPopupId);
//            PopupCertiDto popupCertiDto = PopupCertiDto.fromEntity(popup.getName(), popup.getPosterUrl(),
//                    visit.getCreatedAt());
//            popupCertiDtoList.add(popupCertiDto);
//        }
//        return popupCertiDtoList;
//    }

    public List<UserFaqResponseDto> readFAQs() {
        List<FreqQuestion> freqQuestionList = freqQuestionRepository.findAllByOrderByCreatedAtDesc();
        List<UserFaqResponseDto> faqDtoList = new ArrayList<>();
        for (FreqQuestion freqQuestion : freqQuestionList) {
            faqDtoList.add(UserFaqResponseDto.builder()
                    .faqId(String.valueOf(freqQuestion.getId()))
                    .question(freqQuestion.getQuestion())
                    .answer(freqQuestion.getAnswer())
                    .createdAt(freqQuestion.getCreatedAt().toString())
                    .build());
        }
        return faqDtoList;
    }

    public UserNicknameResponseDto generateRandomNickname() {
        String randomNickname = RandomNicknameUtil.generateRandomNickname();
        return new UserNicknameResponseDto(randomNickname);
    }

    public void addReviewCnt(User user) {
        user.addReviewCnt();
        userQueryRepository.save(user);
    }
}
