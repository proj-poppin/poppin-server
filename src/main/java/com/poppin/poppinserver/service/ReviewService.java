package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.visitorData.common.Congestion;
import com.poppin.poppinserver.dto.visitorData.common.Satisfaction;
import com.poppin.poppinserver.dto.visitorData.common.VisitDate;
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private final S3Service s3Service;

    /*후기 생성*/
    @Transactional
    public ReviewDto createReview(CreateReviewDto createReviewDto, List<MultipartFile> images) {

        User user = userRepository.findById(1L)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(createReviewDto.popupId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        Review review = Review.builder()
                .user(user)
                .popup(popup)
                .text(createReviewDto.text())
                .isCertificated(createReviewDto.isCertificated())
                .build();

        review = reviewRepository.save(review);

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

        review = reviewRepository.save(review);

        VisitorData visitorData = new VisitorData(
                VisitDate.fromValue(createReviewDto.visitDate())
                , popup
                , review
                , Congestion.fromValue(createReviewDto.congestion())
                , Satisfaction.fromValue(createReviewDto.satisfaction())
        );

        visitorDataRepository.save(visitorData);

        return ReviewDto.fromEntity(reviewRepository.save(review), visitorData);
    }

    /*후기 추천 증가*/
    public String addRecommendReview(Long userId ,Long reviewId, Long popupId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Review review = reviewRepository.findByReviewIdAndPopupId(reviewId, popupId);

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

    /*작성완료 후기 조회*/
    public List<Review> readFinishReview(Long userId){
        List<Review> reviewList = reviewRepository.findByUserId(userId);

        /*개발 중*/
//        for (Review review : reviewList){
//            Popup popup = popupRepository.f
//            VisitorData visitorData = visitorDataRepository.findBy(review);
//        }

        return reviewList;
    }
}
