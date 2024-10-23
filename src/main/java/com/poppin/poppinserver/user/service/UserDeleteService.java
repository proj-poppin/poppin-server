package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.alarm.repository.NotificationRepository;
import com.poppin.poppinserver.inform.repository.ManagerInformRepository;
import com.poppin.poppinserver.inform.repository.ModifyInformRepository;
import com.poppin.poppinserver.inform.repository.UserInformRepository;
import com.poppin.poppinserver.interest.repository.InterestRepository;
import com.poppin.poppinserver.modifyInfo.domain.ModifyInfo;
import com.poppin.poppinserver.modifyInfo.repository.ModifyImageReposiroty;
import com.poppin.poppinserver.popup.repository.BlockedPopupRepository;
import com.poppin.poppinserver.popup.service.S3Service;
import com.poppin.poppinserver.report.repository.ReportPopupRepository;
import com.poppin.poppinserver.report.repository.ReportReviewRepository;
import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.review.domain.ReviewImage;
import com.poppin.poppinserver.review.repository.ReviewImageRepository;
import com.poppin.poppinserver.review.repository.ReviewRecommendRepository;
import com.poppin.poppinserver.review.repository.ReviewRepository;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.BlockedUserRepository;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import com.poppin.poppinserver.visit.repository.VisitRepository;
import com.poppin.poppinserver.visit.repository.VisitorDataRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserDeleteService {
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
    private final S3Service s3Service;
    private final VisitRepository visitRepository;
    private final VisitorDataRepository visitorDataRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final UserQueryUseCase userQueryUseCase;
    private final UserQueryRepository userQueryRepository;

    public void deleteUser(Long userId) {
        User user = userQueryUseCase.findUserById(userId);
        user.softDelete();
        userQueryRepository.save(user);
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
}
