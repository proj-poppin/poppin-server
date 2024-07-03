package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.type.ECongestion;
import com.poppin.poppinserver.type.ESatisfaction;
import com.poppin.poppinserver.type.EVisitDate;
import com.poppin.poppinserver.dto.review.response.ReviewDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.*;
import jakarta.transaction.Transactional;
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
    private final ReviewRecommendUserRepository reviewRecommendUserRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final VisitorDataRepository visitorDataRepository;
    private final VisitRepository visitRepository;
    private final S3Service s3Service;
    private final UserService userService;



    /*방문(인증)후기 생성*/
    @Transactional
    public ReviewDto writeCertifiedReview(Long userId, Long popupId,String text, String visitDate, String satisfaction, String congestion, String nickname, List<MultipartFile> images) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        /*방문 내역 확인*/
        Optional<Visit> visit = visitRepository.findByUserId(userId, popup.getId());
        if (visit.isEmpty())throw new CommonException(ErrorCode.NOT_FOUND_VISIT);

        Review review = Review.builder()
                .user(user)
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

            log.info("images 객체 : " + images);
            log.info("images 사이즈 : " + images.size());
            log.info("images 첫번째 요소 파일 명: " + images.get(0).getOriginalFilename());

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
        log.info("image Status : " + imageStatus);

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
    public ReviewDto writeUncertifiedReview(Long userId, Long popupId,String text, String visitDate, String satisfaction, String congestion, String nickname, List<MultipartFile> images) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        Review review = Review.builder()
                .user(user)
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

            log.info("images Entity : " + images);
            log.info("images Size : " + images.size());
            log.info("images first img name: " + images.get(0).getOriginalFilename());

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
        log.info("image Status : " + imageStatus);
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
    public String addRecommendReview(Long userId ,Long reviewId, Long popupId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Review review = reviewRepository.findByReviewIdAndPopupId(reviewId, popupId);

        // 예외처리
        if (review == null)throw new CommonException(ErrorCode.NOT_FOUND_REVIEW);
        if (review.getUser().getId().equals(userId)) throw new CommonException(ErrorCode.REVIEW_RECOMMEND_ERROR);

        Optional<ReviewRecommendUser> recommendCnt = reviewRecommendUserRepository.findByUserAndReview(user, review);
        if (recommendCnt.isPresent())throw new CommonException(ErrorCode.DUPLICATED_RECOMMEND_COUNT); // 2회이상 같은 후기에 대해 추천 증가 방지
        else review.addRecommendCnt();

        reviewRepository.save(review);

        ReviewRecommendUser reviewRecommendUser = ReviewRecommendUser.builder()
                .user(user)
                .review(review)
                .build();
        reviewRecommendUserRepository.save(reviewRecommendUser);

        return "정상적으로 반환되었습니다";
    }

}
