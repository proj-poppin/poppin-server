package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.type.ECongestion;
import com.poppin.poppinserver.type.ESatisfaction;
import com.poppin.poppinserver.type.EVisitDate;
import com.poppin.poppinserver.dto.review.request.CreateReviewDto;
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

    /*방문(인증)후기 생성*/
    @Transactional
    public ReviewDto createCertifiedReview(Long userId, CreateReviewDto createReviewDto, List<MultipartFile> images) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(createReviewDto.popupId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        /*방문 내역 확인*/
        Optional<Visit> visit = visitRepository.findByUserId(userId, popup.getId());
        if (visit.isEmpty())throw new CommonException(ErrorCode.NOT_FOUND_VISIT);

        Review review = Review.builder()
                .user(user)
                .popup(popup)
                .text(createReviewDto.text())
                .isCertificated(true)
                .build();

        review = reviewRepository.save(review);

        // 이미지 없을 시 넘어감
        if (images.isEmpty()) {

        }else{
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

        VisitorData visitorData = new VisitorData(
                EVisitDate.fromValue(createReviewDto.visitDate())
                , popup
                , review
                , ECongestion.fromValue(createReviewDto.congestion())
                , ESatisfaction.fromValue(createReviewDto.satisfaction())
        );

        visitorDataRepository.save(visitorData);

        return ReviewDto.fromEntity(reviewRepository.save(review), visitorData);
    }

    @Transactional
    public ReviewDto createUncertifiedReview(Long userId, CreateReviewDto createReviewDto, List<MultipartFile> images) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Popup popup = popupRepository.findById(createReviewDto.popupId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        Review review = Review.builder()
                .user(user)
                .popup(popup)
                .text(createReviewDto.text())
                .isCertificated(false)
                .build();

        review = reviewRepository.save(review);

        // 이미지 없을 시 넘어감
        if (images.isEmpty()) {

        }else{
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

        VisitorData visitorData = new VisitorData(
                EVisitDate.fromValue(createReviewDto.visitDate())
                , popup
                , review
                , ECongestion.fromValue(createReviewDto.congestion())
                , ESatisfaction.fromValue(createReviewDto.satisfaction())
        );

        visitorDataRepository.save(visitorData);

        return ReviewDto.fromEntity(reviewRepository.save(review), visitorData);
    }


    /*후기 추천 증가*/
    public String addRecommendReview(Long userId ,Long reviewId, Long popupId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Review review = reviewRepository.findByReviewIdAndPopupId(reviewId, popupId);
        if (review == null)throw new CommonException(ErrorCode.NOT_FOUND_REVIEW);

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
