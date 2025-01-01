package com.poppin.poppinserver.review.service;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.popup.repository.PosterImageRepository;
import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.review.dto.response.ReviewListDto;
import com.poppin.poppinserver.review.repository.ReviewQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PosterImageRepository posterImageRepository;


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
                            review.getIsCertificated(),
                            review.getCreatedAt(),
                            imageUrl
                    );
                })
                .collect(Collectors.toList());
    }

}
