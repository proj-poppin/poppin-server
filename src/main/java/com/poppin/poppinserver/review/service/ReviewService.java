package com.poppin.poppinserver.review.service;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.popup.repository.PosterImageRepository;
import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.review.dto.response.ReviewDto;
import com.poppin.poppinserver.review.dto.response.ReviewListDto;
import com.poppin.poppinserver.review.repository.ReviewImageQueryRepository;
import com.poppin.poppinserver.review.repository.ReviewQueryRepository;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import com.poppin.poppinserver.visit.domain.VisitorData;
import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataRvDto;
import com.poppin.poppinserver.visit.repository.VisitRepository;
import com.poppin.poppinserver.visit.repository.VisitorDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewQueryRepository reviewQueryRepository;

    private final PopupRepository popupRepository;
    private final VisitRepository visitRepository;
    private final VisitorDataRepository visitorDataRepository;
    private final PosterImageRepository posterImageRepository;
    private final ReviewImageQueryRepository reviewImageQueryRepository;

    private final UserQueryUseCase userQueryUseCase;


    public List<ReviewListDto> readReviewList(Long userId) {

        List<Review> reviewList = reviewQueryRepository.findByUserIdWithPopup(userId);

        List<Long> popupIds = reviewList.stream()
                .map(review -> review.getPopup().getId())
                .collect(Collectors.toList());

        Map<Long, String> popupImageMap = posterImageRepository.findAllByPopupIds(popupIds).stream()
                .collect(Collectors.toMap(
                        posterImage -> posterImage.getPopupId().getId(),
                        PosterImage::getPosterUrl,
                        (existing, replacement) -> existing
                ));

        return reviewList.stream()
                .map(review -> {
                    Popup popup = review.getPopup();
                    String imageUrl = popupImageMap.getOrDefault(popup.getId(), null);
                    return ReviewListDto.fromEntity(
                            review.getId(),
                            popup.getId(),
                            popup.getName(),
                            review.getIsCertified(),
                            review.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                            imageUrl
                    );
                })
                .collect(Collectors.toList());
    }


    public ReviewDto readReview(Long userId, String strReviewId) {

        Long reviewId = Long.valueOf(strReviewId);

        User user = userQueryUseCase.findUserById(userId);

        Review review = reviewQueryRepository.findById(reviewId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_REVIEW));

        Popup popup = popupRepository.findById(review.getPopup().getId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        boolean isCertified = visitRepository.findByUserId(userId, popup.getId()).isPresent();

        String visitCreatedAt = isCertified
                ? visitRepository.findByUserId(userId, popup.getId()).get().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                : null;

        VisitorData visitorData = visitorDataRepository.findByReviewIdAndPopupId(reviewId, popup.getId());
        VisitorDataRvDto visitorDataRvDto = visitorData != null
                ? VisitorDataRvDto.fromEntity(visitorData.getVisitDate(), visitorData.getSatisfaction(), visitorData.getCongestion())
                : null;

        List<String> reviewImageListUrl = reviewImageQueryRepository.findUrlAllByReviewId(reviewId);

        return ReviewDto.fromEntity(
                popup.getName(),
                popup.getPosterUrl(),
                isCertified,
                user.getNickname(),
                visitCreatedAt,
                review.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                visitorDataRvDto,
                review.getText(),
                reviewImageListUrl
        );
    }
}
