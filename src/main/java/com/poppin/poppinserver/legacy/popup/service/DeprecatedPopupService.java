//package com.poppin.poppinserver.legacy.popup;
//
//import com.poppin.poppinserver.core.exception.CommonException;
//import com.poppin.poppinserver.core.exception.ErrorCode;
//import com.poppin.poppinserver.popup.domain.Popup;
//import com.poppin.poppinserver.popup.domain.PosterImage;
//import com.poppin.poppinserver.popup.dto.popup.response.PopupDetailDto;
//import com.poppin.poppinserver.popup.dto.popup.response.PopupGuestDetailDto;
//import com.poppin.poppinserver.popup.dto.popup.response.PopupStoreDto;
//import com.poppin.poppinserver.review.domain.Review;
//import com.poppin.poppinserver.review.domain.ReviewImage;
//import com.poppin.poppinserver.review.dto.response.ReviewInfoDto;
//import com.poppin.poppinserver.user.domain.User;
//import com.poppin.poppinserver.visit.domain.Visit;
//import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataInfoDto;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
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
//
//    public List<PopupStoreDto> guestGetPopupStoreDtos(List<Popup> popups) {
//        if (popups == null || popups.isEmpty()) {
//            return null;
//        }
//        // 방문자 데이터 리스트 및 실시간 방문자 수 리스트 생성
//        List<VisitorDataInfoDto> visitorDataInfoDtos = new ArrayList<>();
//        List<Optional<Integer>> visitorCntList = new ArrayList<>();
//
//        // 각 Popup에 대해 방문자 데이터 및 실시간 방문자 수를 조회하여 리스트에 추가
//        for (Popup popup : popups) {
//            VisitorDataInfoDto visitorDataDto = visitorDataQueryUseCase.findVisitorData(popup.getId()); // 방문자 데이터
//            visitorDataInfoDtos.add(visitorDataDto);
//
//            Optional<Integer> visitorCnt = visitQueryUseCase.getRealTimeVisitors(popup.getId()); // 실시간 방문자 수
//            visitorCntList.add(visitorCnt);
//        }
//
//        // PopupStoreDto 리스트를 생성하여 반환
//        return PopupStoreDto.fromEntities(popups, visitorDataInfoDtos, visitorCntList);
//    }
//
//    public List<PopupStoreDto> getPopupStoreDtos(List<Popup> popups, Long userId) {
//        if (popups == null || popups.isEmpty()) {
//            return null;
//        }
//        // 방문자 데이터 리스트 및 실시간 방문자 수 리스트 생성
//        List<VisitorDataInfoDto> visitorDataInfoDtos = new ArrayList<>();
//        List<Optional<Integer>> visitorCntList = new ArrayList<>();
//        List<Boolean> isBlockedList = new ArrayList<>();
//        List<LocalDateTime> interestCreatedAtList = new ArrayList<>();
//
//        // 각 Popup에 대해 방문자 데이터 및 실시간 방문자 수를 조회하여 리스트에 추가
//        for (Popup popup : popups) {
//            VisitorDataInfoDto visitorDataDto =  visitorDataQueryUseCase.findVisitorData(popup.getId()); // 방문자 데이터
//            visitorDataInfoDtos.add(visitorDataDto);
//
//            Optional<Integer> visitorCnt = visitQueryUseCase.getRealTimeVisitors(popup.getId()); // 실시간 방문자 수
//            visitorCntList.add(visitorCnt);
//
//            Boolean idBlocked = blockedPopupRepository.existsByPopupIdAndUserId(popup.getId(), userId);
//            isBlockedList.add(idBlocked);
//
//            LocalDateTime interestCreatedAt = interestRepository.findCreatedAtByUserIdAndPopupId(userId, popup.getId());
//            interestCreatedAtList.add(interestCreatedAt);
//        }
//
//        // PopupStoreDto 리스트를 생성하여 반환
//        return PopupStoreDto.fromEntities(popups, visitorDataInfoDtos, visitorCntList, isBlockedList, interestCreatedAtList);
//    }
//}
