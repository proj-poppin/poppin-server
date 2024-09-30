package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.alarm.repository.NotificationRepository;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.util.RandomNicknameUtil;
import com.poppin.poppinserver.inform.repository.ManagerInformRepository;
import com.poppin.poppinserver.inform.repository.ModifyInformRepository;
import com.poppin.poppinserver.inform.repository.UserInformRepository;
import com.poppin.poppinserver.interest.repository.InterestRepository;
import com.poppin.poppinserver.modifyInfo.domain.ModifyInfo;
import com.poppin.poppinserver.modifyInfo.repository.ModifyImageReposiroty;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.popup.dto.popup.response.PopupCertiDto;
import com.poppin.poppinserver.popup.repository.BlockedPopupRepository;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.popup.repository.PosterImageRepository;
import com.poppin.poppinserver.popup.service.S3Service;
import com.poppin.poppinserver.report.repository.ReportPopupRepository;
import com.poppin.poppinserver.report.repository.ReportReviewRepository;
import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.review.domain.ReviewImage;
import com.poppin.poppinserver.review.dto.response.ReviewCertiDto;
import com.poppin.poppinserver.review.dto.response.ReviewFinishDto;
import com.poppin.poppinserver.review.dto.response.ReviewUncertiDto;
import com.poppin.poppinserver.review.repository.ReviewImageRepository;
import com.poppin.poppinserver.review.repository.ReviewRecommendRepository;
import com.poppin.poppinserver.review.repository.ReviewRepository;
import com.poppin.poppinserver.user.domain.FreqQuestion;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.dto.faq.response.FaqResponseDto;
import com.poppin.poppinserver.user.dto.user.request.UpdateUserInfoDto;
import com.poppin.poppinserver.user.dto.user.response.NicknameDto;
import com.poppin.poppinserver.user.dto.user.response.UserMypageDto;
import com.poppin.poppinserver.user.dto.user.response.UserProfileDto;
import com.poppin.poppinserver.user.repository.BlockedUserRepository;
import com.poppin.poppinserver.user.repository.FreqQuestionRepository;
import com.poppin.poppinserver.user.repository.UserRepository;
import com.poppin.poppinserver.visit.domain.Visit;
import com.poppin.poppinserver.visit.domain.VisitorData;
import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataRvDto;
import com.poppin.poppinserver.visit.repository.VisitRepository;
import com.poppin.poppinserver.visit.repository.VisitorDataRepository;
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
    private final ReviewRepository reviewRepository;
    private final PopupRepository popupRepository;
    private final VisitRepository visitRepository;
    private final VisitorDataRepository visitorDataRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final S3Service s3Service;
    private final FreqQuestionRepository freqQuestionRepository;
    private final PosterImageRepository posterImageRepository;
    private final BlockedUserRepository blockedUserRepository;
    private final InterestRepository interestRepository;
    private final ReviewRecommendRepository reviewRecommendRepository;
    private final UserInformRepository userInformRepository;
    private final ManagerInformRepository managerInformRepository;
    private final ModifyInformRepository modifyInfoRepository;
    private final ModifyImageReposiroty modifyImageReposiroty;
    private final ReportReviewRepository reportReviewRepository;
    private final ReportPopupRepository reportPopupRepository;
    private final NotificationRepository notificationRepository;
    private final BlockedPopupRepository blockedPopupRepository;


    // TODO: 삭제 예정
    public UserMypageDto readUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        return UserMypageDto.builder()
                .userImageUrl(user.getProfileImageUrl())
                .nickname(user.getNickname())
                .writtenReview(user.getReviewCnt())
                .visitedPopupCnt(user.getVisitedPopupCnt())
                .build();
    }

    // TODO: 삭제 예정
    public UserProfileDto readUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        return UserProfileDto.builder()
                .email(user.getEmail())
                .userImageUrl(user.getProfileImageUrl())
                .nickname(user.getNickname())
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

    public UserProfileDto updateUserNickname(Long userId, UpdateUserInfoDto updateUserInfoDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        if (userRepository.findByNickname(updateUserInfoDto.nickname()).isPresent() && (userId != user.getId())) {
            throw new CommonException(ErrorCode.DUPLICATED_NICKNAME);
        }
        user.updateUserNickname(updateUserInfoDto.nickname());
        userRepository.save(user);

        return UserProfileDto.builder()
                .provider(user.getProvider())
                .userImageUrl(user.getProfileImageUrl())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        user.softDelete();
        userRepository.save(user);
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
        User user = userRepository.findById(userId)
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

    /*마이페이지 - 작성완료 후기 조회 - 일반 후기 보기*/
    public ReviewUncertiDto getUncertifiedReview(Long userId, Long reviewId, Long popupId) {
        User user = userRepository.findById(userId)
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

    public List<PopupCertiDto> getCertifiedPopupList(Long userId) {
        /* 1. userId로 visit 리스트 뽑기
         *  2. visit 리스트의 popupid 와 popup의 id 일치하는 popup 뽑기
         */
        List<Visit> visitList = visitRepository.findAllByUserId(userId);
        if (visitList.isEmpty()) {
            throw new CommonException(ErrorCode.NOT_FOUND_VISIT);
        }

        List<PopupCertiDto> popupCertiDtoList = new ArrayList<>();

        for (Visit visit : visitList) {
            Long vdPopupId = visit.getPopup().getId();
            Popup popup = popupRepository.findTopByPopupId(vdPopupId);
            PopupCertiDto popupCertiDto = PopupCertiDto.fromEntity(popup.getName(), popup.getPosterUrl(),
                    visit.getCreatedAt());
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

    public void deleteAllRelatedInfo(User user) {
        Long userId = user.getId();
        visitRepository.deleteAllByUserId(userId);  // 유저 팝업 방문 삭제
        interestRepository.deleteAllByUserId(userId);  // 유저 팝업 관심 등록 전부 삭제
        reviewRecommendRepository.deleteAllByUserId(userId);    // 유저가 누른 모든 추천 삭제
        deleteUserReports(userId);   // 유저가 남긴 모든 신고 삭제
        deleteUserReviews(userId);  // 유저가 남긴 모든 후기 삭제
        deleteInformRequests(userId);   // 유저가 남긴 모든 제보 삭제
        deleteUserModifyInfoRequests(userId);    // 유저가 남긴 모든 정보수정요청 삭제
        if (user.getProfileImageUrl() != null) {
            s3Service.deleteImage(user.getProfileImageUrl());   // 유저 프로필 이미지 S3에서도 삭제
        }
        deleteUserNotificationAlarmInfo(userId);    // 유저 공지사항 알람 삭제
        deleteBlockedPopups(userId);    // 팝업 차단 목록 삭제
        deleteBlockedUsers(userId);    // 유저 차단 목록 삭제
    }

    /*
        유저가 작성한 모든 후기 삭제
     */
    private void deleteUserReviews(Long userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);

        // 후기 이미지 삭제
        for (Review review : reviews) {
            Long reviewId = review.getId();
            List<ReviewImage> reviewImages = reviewImageRepository.findAllByReviewId(reviewId);
            // S3에서 삭제
            for (ReviewImage reviewImage : reviewImages) {
                s3Service.deleteImage(reviewImage.getImageUrl());
                log.info("Deleting image from S3: {}", reviewImage.getImageUrl());
            }
            // DB에서 삭제
            reviewImageRepository.deleteAllByReviewId(reviewId);
            log.info("Deleting review images from DB for reviewId: {}", reviewId);
            // 방문자 데이터 삭제
            deleteUserVisitData(reviewId);
            // 후기 추천 삭제
            deleteReviewRecommend(reviewId);
            // 후기 신고 삭제
            reportReviewRepository.deleteAllByReviewId(reviewId);
        }

        // 모든 후기 삭제
        reviewRepository.deleteAllByUserId(userId);
        log.info("Finished deleting reviews for userId: {}", userId);
    }

    /*
        유저 방문자 데이터 삭제
    */
    private void deleteUserVisitData(Long reviewId) {
        visitorDataRepository.deleteAllByReviewId(reviewId);
    }

    /*
        유저 후기 추천 삭제
     */
    private void deleteReviewRecommend(Long reviewId) {
        reviewRecommendRepository.deleteAllByReviewId(reviewId);
    }

    /*
        유저가 작성한 모든 제보 삭제
     */
    private void deleteInformRequests(Long userId) {
        userInformRepository.deleteAllByInformerId(userId);
        managerInformRepository.deleteAllByInformerId(userId);
    }

    /*
        유저가 작성한 모든 정보수정요청 삭제
     */
    private void deleteUserModifyInfoRequests(Long userId) {
        List<ModifyInfo> modifyInfos = modifyInfoRepository.findAllByUserId(userId);
        for (ModifyInfo modifyInfo : modifyInfos) {
            Long modifyId = modifyInfo.getId();
            modifyImageReposiroty.deleteAllByModifyId(modifyId);
        }
        modifyInfoRepository.deleteAllByUserId(userId);
    }

    /*
        유저가 작성한 모든 신고 삭제
     */
    private void deleteUserReports(Long userId) {
        reportReviewRepository.deleteAllByUserId(userId);
        reportPopupRepository.deleteAllByUserId(userId);
    }

    /*
        유저 차단 목록 삭제
     */
    private void deleteBlockedUsers(Long userId) {
        blockedUserRepository.deleteAllByUserId(userId);
        blockedUserRepository.deleteAllByBlockedId(userId);
    }

    /*
        유저 공지 사항 알람 정보 삭제
     */
    private void deleteUserNotificationAlarmInfo(Long userId) {
        notificationRepository.deleteAllByUserId(userId);
    }

    /*
        팝업 차단 목록 삭제
     */
    private void deleteBlockedPopups(Long userId) {
        blockedPopupRepository.deleteAllByUserId(userId);
    }

    public void addReviewCnt(User user) {
        user.addReviewCnt();
        userRepository.save(user);
    }
}
