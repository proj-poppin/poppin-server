//package com.poppin.poppinserver.legacy.popup;
//
//import com.poppin.poppinserver.core.exception.CommonException;
//import com.poppin.poppinserver.core.exception.ErrorCode;
//import com.poppin.poppinserver.popup.domain.Popup;
//import com.poppin.poppinserver.popup.domain.PosterImage;
//import com.poppin.poppinserver.popup.dto.popup.response.PopupDetailDto;
//import com.poppin.poppinserver.popup.dto.popup.response.PopupGuestDetailDto;
//import com.poppin.poppinserver.review.domain.Review;
//import com.poppin.poppinserver.review.domain.ReviewImage;
//import com.poppin.poppinserver.review.dto.response.ReviewInfoDto;
//import com.poppin.poppinserver.user.domain.User;
//import com.poppin.poppinserver.visit.domain.Visit;
//import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataInfoDto;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//public class DeprecatedPopupService {
//    public PopupGuestDetailDto readGuestDetail(String strPopupId) {
//        Long popupId = Long.valueOf(strPopupId);
//
//        Popup popup = popupRepository.findById(popupId)
//                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));
//
//        popup.addViewCnt(); // 조회수 + 1
//
//        List<Review> reviews = reviewRepository.findAllByPopupIdOrderByRecommendCntDesc(popupId);
//
//        // 리뷰 이미지 목록 가져오기
//        List<List<String>> reviewImagesList = new ArrayList<>();
//        List<String> profileImagesList = new ArrayList<>();
//        List<Integer> reviewCntList = new ArrayList<>();
//
//        for (Review review : reviews) {
//            List<ReviewImage> reviewImages = reviewImageQueryUseCase.findAllByReviewId(review.getId());
//
//            List<String> imagesList = new ArrayList<>();
//            for (ReviewImage reviewImage : reviewImages) {
//                imagesList.add(reviewImage.getImageUrl());
//            }
//
//            reviewImagesList.add(imagesList);
//            profileImagesList.add(review.getUser().getProfileImageUrl());
//            reviewCntList.add(review.getUser().getReviewCnt());
//        }
//
//        List<ReviewInfoDto> reviewInfoList = ReviewInfoDto.fromEntityList(reviews, reviewImagesList, profileImagesList,
//                reviewCntList);
//
//        VisitorDataInfoDto visitorDataDto = visitorDataQueryUseCase.findVisitorData(popupId); // 방문자 데이터
//
//        Optional<Integer> visitors = visitQueryUseCase.getRealTimeVisitors(popupId); // 실시간 방문자
//
//        popupRepository.save(popup);
//
//        // 이미지 목록 가져오기
//        List<PosterImage> posterImages = posterImageRepository.findByPopupId(popup);
//
//        List<String> imageList = new ArrayList<>();
//        for (PosterImage posterImage : posterImages) {
//            imageList.add(posterImage.getPosterUrl());
//        }
//
//        return PopupGuestDetailDto.fromEntity(popup, imageList, reviewInfoList, visitorDataDto, visitors);
//    } // 비로그인 상세조회
//
//    @Transactional
//    public PopupDetailDto readDetail(String strPopupId, Long userId) {
//        Long popupId = Long.valueOf(strPopupId);
//
//        Popup popup = popupRepository.findById(popupId)
//                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));
//
//        popup.addViewCnt(); // 조회수 + 1
//
//        List<Review> reviews = reviewRepository.findAllByPopupIdOrderByRecommendCntDesc(popupId);
//
//        List<Long> blockedUserIds = blockedUserQueryRepository.findBlockedUserIdsByUserId(userId);
//        log.info("Blocked User IDs: " + blockedUserIds.toString());
//
//        // 차단된 사용자의 리뷰를 제외한 리스트
//        List<Review> filteredReviews = new ArrayList<>();
//        // 리뷰 이미지 목록, 프로필 이미지 가져오기
//        List<List<String>> reviewImagesList = new ArrayList<>();
//        List<String> profileImagesList = new ArrayList<>();
//        List<Integer> reviewCntList = new ArrayList<>();
//
//        for (Review review : reviews) {
//            if (blockedUserIds.contains(review.getUser().getId())) {
//                log.info("Filtered Review by User ID: " + review.getUser().getId());
//                continue;
//            }
//
//            filteredReviews.add(review);
//
//            List<ReviewImage> reviewImages = reviewImageQueryUseCase.findAllByReviewId(review.getId());
//
//            List<String> imagesList = new ArrayList<>();
//            for (ReviewImage reviewImage : reviewImages) {
//                imagesList.add(reviewImage.getImageUrl());
//            }
//
//            reviewImagesList.add(imagesList);
//            profileImagesList.add(review.getUser().getProfileImageUrl());
//            reviewCntList.add(review.getUser().getReviewCnt());
//        }
//
//        List<ReviewInfoDto> reviewInfoList = ReviewInfoDto.fromEntityList(filteredReviews, reviewImagesList,
//                profileImagesList, reviewCntList);
//
//        VisitorDataInfoDto visitorDataDto =  visitorDataQueryUseCase.findVisitorData(popupId); // 방문자 데이터
//
//        Optional<Integer> visitors = visitQueryUseCase.getRealTimeVisitors(popupId); // 실시간 방문자
//
//        popupRepository.save(popup);
//
//        // 이미지 목록 가져오기
//        List<PosterImage> posterImages = posterImageRepository.findAllByPopupId(popup);
//
//        List<String> imageList = new ArrayList<>();
//        for (PosterImage posterImage : posterImages) {
//            imageList.add(posterImage.getPosterUrl());
//        }
//
//        // 관심 여부 확인
//        Boolean isInterested = interestQueryUseCase.existsInterestByUserIdAndPopupId(userId, popupId);
//
//        Optional<Visit> visit = visitQueryUseCase.findByUserId(userId, popupId);
//
//        // 차단 여부 확인
//        User user = userQueryUseCase.findUserById(userId);
//        Boolean isBlocked = blockedPopupRepository.findByPopupIdAndUserId(popup, user).isPresent();
//
//        // 방문 여부 확인
//        if (!visit.equals(null)) {
//            return PopupDetailDto.fromEntity(popup, imageList, isInterested, reviewInfoList, visitorDataDto, visitors,
//                    true, isBlocked); // 이미 방문함
//        } else {
//            return PopupDetailDto.fromEntity(popup, imageList, isInterested, reviewInfoList, visitorDataDto, visitors,
//                    false, isBlocked); // 방문 한적 없음
//        }
//    } // 로그인 상세조회
//}
