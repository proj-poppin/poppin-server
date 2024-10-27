package com.poppin.poppinserver.review.service;

import com.poppin.poppinserver.alarm.domain.FCMToken;
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
import com.poppin.poppinserver.review.dto.response.ReviewDto;
import com.poppin.poppinserver.review.repository.ReviewCommandRepository;
import com.poppin.poppinserver.review.repository.ReviewImageCommandRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ReviewWriteService {

    private final UserQueryRepository userQueryRepository;
    private final PopupRepository popupRepository;
    private final ReviewCommandRepository reviewRepository;
    private final ReviewImageCommandRepository reviewImageRepository;
    private final VisitorDataRepository visitorDataRepository;
    private final VisitRepository visitRepository;
    private final FCMTokenRepository fcmTokenRepository;
    private final S3Service s3Service;
    private final UserService userService;


    @Transactional
    public ReviewDto writeReview(Long userId, Long popupId, String text, String visitDate,
                                 String satisfaction, String congestion,
                                 List<MultipartFile> images) {

        User user = userQueryRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        // 방문 내역 확인 및 리뷰 생성
        boolean isCertificated = visitRepository.findByUserId(userId, popup.getId()).isPresent();

        // 알림 토큰 추출
        Optional<FCMToken> fcmToken = fcmTokenRepository.findByUserId(userId);
        if (fcmToken.isEmpty()) {
            throw new CommonException(ErrorCode.REVIEW_FCM_ERROR);
        }

        String token = fcmToken.get().getToken();
        Review review = createReview(user, token, popup, text, isCertificated);

        // 이미지 처리
        handleReviewImages(images, review);

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
        userService.addReviewCnt(user);

        return ReviewDto.fromEntity(review, visitorData);
    }

    private Review createReview(User user, String token, Popup popup, String text, boolean isCertificated) {
        Review review = Review.builder()
                .user(user)
                .token(token)
                .popup(popup)
                .text(text)
                .isCertificated(isCertificated)
                .build();
        return reviewRepository.save(review);
    }

    private void handleReviewImages(List<MultipartFile> images, Review review) {
        if (images.isEmpty() || "empty".equals(images.get(0).getOriginalFilename())) {
            return;
        }

        log.info("images 객체 : {}", images);
        log.info("images 사이즈 : {}", images.size());
        log.info("images 첫번째 요소 파일 명: {}", images.get(0).getOriginalFilename());

        List<String> fileUrls = s3Service.uploadReviewImage(images, review.getId());
        List<ReviewImage> posterImages = fileUrls.stream()
                .map(url -> ReviewImage.builder().imageUrl(url).review(review).build())
                .collect(Collectors.toList());

        reviewImageRepository.saveAll(posterImages);
        review.updateReviewUrl(fileUrls.get(0));
    }

}
