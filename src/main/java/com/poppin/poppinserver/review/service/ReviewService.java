package com.poppin.poppinserver.review.service;

import com.poppin.poppinserver.alarm.service.AlarmService;
import com.poppin.poppinserver.alarm.service.FCMSendService;
import com.poppin.poppinserver.core.type.*;
import com.poppin.poppinserver.alarm.dto.fcm.request.FCMRequestDto;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.review.domain.ReviewImage;
import com.poppin.poppinserver.review.domain.ReviewRecommend;
import com.poppin.poppinserver.review.dto.review.response.ReviewDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.review.repository.ReviewImageRepository;
import com.poppin.poppinserver.review.repository.ReviewRecommendRepository;
import com.poppin.poppinserver.review.repository.ReviewRepository;
import com.poppin.poppinserver.popup.service.S3Service;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.UserRepository;
import com.poppin.poppinserver.user.service.UserService;
import com.poppin.poppinserver.visit.domain.Visit;
import com.poppin.poppinserver.visit.domain.VisitorData;
import com.poppin.poppinserver.visit.repository.VisitRepository;
import com.poppin.poppinserver.visit.repository.VisitorDataRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final UserRepository userRepository;
    private final PopupRepository popupRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewRecommendRepository reviewRecommendRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final VisitorDataRepository visitorDataRepository;
    private final VisitRepository visitRepository;
    private final S3Service s3Service;
    private final UserService userService;
    private final FCMSendService fcmSendService;
    private final AlarmService alarmService;



    /*방문(인증)후기 생성*/
    @Transactional
    public ReviewDto writeCertifiedReview(Long userId, String token, Long popupId,String text, String visitDate, String satisfaction, String congestion, String nickname, List<MultipartFile> images) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        /*방문 내역 확인*/
        Optional<Visit> visit = visitRepository.findByUserId(userId, popup.getId());
        if (visit.isEmpty())throw new CommonException(ErrorCode.NOT_FOUND_VISIT);

        Review review = Review.builder()
                .user(user)
                .token(token)
                .popup(popup)
                .text(text)
                .isCertificated(true)
                .build();

        review = reviewRepository.save(review);

        String imageStatus = "0";
        log.info("imageStatus : " + imageStatus);

        // 클라이언트 req
        // 이미지 없을 때 -> "empty"명인 빈 파일 전송
        // 이미지 있을 때 -> 이미지 파일 전송
        if (!images.get(0).getOriginalFilename().equals("empty")) {

            log.info("images 객체 : {}", images);
            log.info("images 사이즈 : {}", images.size());
            log.info("images 첫번째 요소 파일 명: {}", images.get(0).getOriginalFilename());

            // 리뷰 이미지 처리 및 저장
            List<String> fileUrls = s3Service.uploadReviewImage(images, review.getId());

            List<ReviewImage> posterImages = new ArrayList<>();
            for (String url : fileUrls) {
                ReviewImage posterImage = ReviewImage.builder()
                        .imageUrl(url)
                        .review(review)
                        .build();
                posterImages.add(posterImage);
            }
            reviewImageRepository.saveAll(posterImages);
            review.updateReviewUrl(fileUrls.get(0));
        }
        log.info("image Status : {}",imageStatus);

        VisitorData visitorData = new VisitorData(
                EVisitDate.fromValue(visitDate)
                , popup
                , review
                , ECongestion.fromValue(congestion)
                , ESatisfaction.fromValue(satisfaction)
        );

        visitorDataRepository.save(visitorData);

        userService.addReviewCnt(user);

        return ReviewDto.fromEntity(reviewRepository.save(review), visitorData);
    }

    @Transactional
    public ReviewDto writeUncertifiedReview(Long userId, String token, Long popupId,String text, String visitDate, String satisfaction, String congestion, String nickname, List<MultipartFile> images) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        Review review = Review.builder()
                .user(user)
                .token(token)
                .popup(popup)
                .text(text)
                .isCertificated(false)
                .build();

        review = reviewRepository.save(review);

        String imageStatus = "0";

        // 클라이언트 req
        // 이미지 없을 때 -> "empty"명인 빈 파일 전송
        // 이미지 있을 때 -> 이미지 파일 전송
        if (!images.get(0).getOriginalFilename().equals("empty")) {

            log.info("images Entity : {}", images);
            log.info("images Size : {}", images.size());
            log.info("images first img name: {}",  images.get(0).getOriginalFilename());

            imageStatus = "1"; // 이미지가 null 이 아닐때

            // 리뷰 이미지 처리 및 저장
            List<String> fileUrls = s3Service.uploadReviewImage(images, review.getId());

            List<ReviewImage> posterImages = new ArrayList<>();
            for (String url : fileUrls) {
                ReviewImage posterImage = ReviewImage.builder()
                        .imageUrl(url)
                        .review(review)
                        .build();
                posterImages.add(posterImage);
            }
            reviewImageRepository.saveAll(posterImages);
            review.updateReviewUrl(fileUrls.get(0));
        }
        log.info("image Status : {} ", imageStatus);
        VisitorData visitorData = new VisitorData(
                EVisitDate.fromValue(visitDate)
                , popup
                , review
                , ECongestion.fromValue(congestion)
                , ESatisfaction.fromValue(satisfaction)
        );

        visitorDataRepository.save(visitorData);

        userService.addReviewCnt(user);

        return ReviewDto.fromEntity(reviewRepository.save(review), visitorData);
    }


    /*후기 추천 증가*/
    public String addRecommendReview(Long userId, Long reviewId, Long popupId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        Review review = reviewRepository.findByReviewIdAndPopupId(reviewId, popupId);

        // 예외처리
        if (review == null)throw new CommonException(ErrorCode.NOT_FOUND_REVIEW);
        if (review.getUser().getId().equals(userId)) throw new CommonException(ErrorCode.REVIEW_RECOMMEND_ERROR);

        Optional<ReviewRecommend> recommendCnt = reviewRecommendRepository.findByUserAndReview(user, review);
        if (recommendCnt.isPresent())throw new CommonException(ErrorCode.DUPLICATED_RECOMMEND_COUNT); // 2회이상 같은 후기에 대해 추천 증가 방지
        else review.addRecommendCnt();

        reviewRepository.save(review);

        ReviewRecommend reviewRecommend = ReviewRecommend.builder()
                .user(user)
                .review(review)
                .build();
        reviewRecommendRepository.save(reviewRecommend);

        // FCM 알림

        FCMRequestDto requestDto = FCMRequestDto.fromEntity(
                popupId,
                review.getToken(),
                EPushInfo.CHOOCHUN.getTitle(),
                "[" + popup.getName() + "] " + EPushInfo.CHOOCHUN.getBody(),
                EPopupTopic.CHOOCHUN
        );

        alarmService.insertPopupAlarm(requestDto); // 저장
        fcmSendService.sendChoochunByFCMToken(review, EPushInfo.CHOOCHUN); // 알림
        return "정상적으로 반환되었습니다";
    }

}
