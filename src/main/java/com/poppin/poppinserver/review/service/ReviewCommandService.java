package com.poppin.poppinserver.review.service;

import com.poppin.poppinserver.alarm.repository.FCMTokenRepository;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.ECongestion;
import com.poppin.poppinserver.core.type.ESatisfaction;
import com.poppin.poppinserver.core.type.EVisitDate;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.popup.service.S3Service;
import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.review.domain.ReviewImage;
import com.poppin.poppinserver.review.dto.response.ReviewWriteDto;
import com.poppin.poppinserver.review.repository.ReviewCommandRepository;
import com.poppin.poppinserver.review.repository.ReviewImageCommandRepository;
import com.poppin.poppinserver.review.repository.ReviewQueryRepository;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import com.poppin.poppinserver.user.service.UserService;
import com.poppin.poppinserver.visit.domain.VisitorData;
import com.poppin.poppinserver.visit.repository.VisitRepository;
import com.poppin.poppinserver.visit.repository.VisitorDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ReviewCommandService {

    private final UserQueryRepository userQueryRepository;
    private final PopupRepository popupRepository;
    private final ReviewCommandRepository reviewCommandRepository;
    private final ReviewQueryRepository reviewQueryRepository;
    private final ReviewImageCommandRepository reviewImageRepository;
    private final VisitorDataRepository visitorDataRepository;
    private final VisitRepository visitRepository;
    private final FCMTokenRepository fcmTokenRepository;
    private final S3Service s3Service;
    private final UserService userService;


    @Transactional
    public ReviewWriteDto writeReview(Long userId, String popupId, String text, String visitDate,
                                      String satisfaction, String congestion,
                                      List<MultipartFile> images) {

        User user = userQueryRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Popup popup = popupRepository.findById(Long.valueOf(popupId))
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        reviewQueryRepository.findByUserIdAndPopupId(userId, Long.valueOf(popupId))
                .ifPresent(review -> {throw new CommonException(ErrorCode.DUPLICATED_REVIEW);});

        // 방문 내역 확인 및 리뷰 생성
        boolean isCertified = visitRepository.findByUserId(userId, popup.getId()).isPresent();

        Review review = createReview(user, popup, text, isCertified);

        // 이미지 처리
        if (images != null && !images.isEmpty()) {
            handleReviewImages(images, review);
        }

        // 방문 데이터 저장
        VisitorData visitorData = new VisitorData(
                EVisitDate.fromValue(visitDate),
                popup,
                review,
                ECongestion.fromValue(congestion),
                ESatisfaction.fromValue(satisfaction)
        );
        visitorDataRepository.save(visitorData);

        // 사용자 리뷰 카운트 증가
        user.addReviewCnt();
        // 후기 작성하기 감소
        user.decreaseVisitedPopupCnt();

        return ReviewWriteDto.fromEntity(review, visitorData);
    }

    private Review createReview(User user, Popup popup, String text, boolean isCertified) {
        Review review = Review.builder()
                .user(user)
                .popup(popup)
                .text(text)
                .isCertified(isCertified)
                .build();
        return reviewCommandRepository.save(review);
    }

    private void handleReviewImages(List<MultipartFile> images, Review review) {
        if (images.isEmpty() || "empty".equals(images.get(0).getOriginalFilename())) {
            return;
        }

        List<String> fileUrls = s3Service.uploadReviewImage(images, review.getId());
        List<ReviewImage> posterImages = fileUrls.stream()
                .map(url -> ReviewImage.builder().imageUrl(url).review(review).build())
                .collect(Collectors.toList());

        reviewImageRepository.saveAll(posterImages);
        review.updateReviewUrl(fileUrls.get(0));
    }

}
