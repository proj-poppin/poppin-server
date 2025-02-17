package com.poppin.poppinserver.review.service;

import com.poppin.poppinserver.alarm.usecase.SendAlarmCommandUseCase;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.EPushInfo;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.review.domain.ReviewRecommend;
import com.poppin.poppinserver.review.repository.ReviewQueryRepository;
import com.poppin.poppinserver.review.repository.ReviewRecommendCommandRepository;
import com.poppin.poppinserver.review.repository.ReviewRecommendQueryRepository;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ReviewRecommendService {

    private final UserQueryRepository userQueryRepository;
    private final PopupRepository popupRepository;
    private final ReviewQueryRepository reviewRepository;
    private final ReviewRecommendCommandRepository reviewRecommendCommandRepository;
    private final ReviewRecommendQueryRepository reviewRecommendQueryRepository;

    private final SendAlarmCommandUseCase sendAlarmCommandUseCase;


    @Transactional
    public String recommendReview(Long userId, String strReviewId, String strPopupId) {
        Long reviewId = Long.parseLong(strReviewId);
        Long popupId = Long.parseLong(strPopupId);

        User user = userQueryRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        Review review = Optional.ofNullable(reviewRepository.findByReviewIdAndPopupId(reviewId, popupId))
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_REVIEW));

        if (review.getUser().getId().equals(userId)) {
            throw new CommonException(ErrorCode.REVIEW_RECOMMEND_ERROR);
        }

        reviewRecommendQueryRepository.findByUserAndReview(user, review).ifPresentOrElse(
                recommend -> {
                    reviewRecommendQueryRepository.delete(recommend);
                    review.decreaseRecommendCnt();
                },
                () -> {
                    review.addRecommendCnt();
                    reviewRecommendCommandRepository.save(new ReviewRecommend(user, review));
                    sendAlarmCommandUseCase.sendChoochunAlarm(user, popup, review, EPushInfo.CHOOCHUN);
                }
        );

        return "정상적으로 반환되었습니다";
    }

}
