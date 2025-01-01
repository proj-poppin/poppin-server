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

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewQueryRepository reviewQueryRepository;

    private final PopupRepository popupRepository;
    private final PosterImageRepository posterImageRepository;


    public List<ReviewListDto> readReviewList(Long userId){
        List<ReviewListDto> reviewListDtoList = new ArrayList<>();
        List<Review> reviewList = reviewQueryRepository.findByUserId(userId);

        for (Review review : reviewList){
            // 팝업 정보
            Popup popup = popupRepository.findByReviewId(review.getPopup().getId());

            // 팝업 이미지 정보
            List<PosterImage> posterImages  = posterImageRepository.findAllByPopupId(popup);
            List<String> imageList = new ArrayList<>();
            if (!posterImages.isEmpty())
            {
                for(PosterImage posterImage : posterImages){
                    imageList.add(posterImage.getPosterUrl());
                }
            }else{
                imageList.add(null);
            }

            ReviewListDto reviewListDto = ReviewListDto.fromEntity(review.getId(), popup.getId(), popup.getName(), review.getIsCertificated(),review.getCreatedAt(),imageList);
            reviewListDtoList.add(reviewListDto);
        }
        return reviewListDtoList;
    }

    public List<?> readReview(Long userId){
        List<ReviewListDto> reviewListDtoList = new ArrayList<>();
        List<Review> reviewList = reviewQueryRepository.findByUserId(userId);

        for (Review review : reviewList){
            // 팝업 정보
            Popup popup = popupRepository.findByReviewId(review.getPopup().getId());

            // 팝업 이미지 정보
            List<PosterImage> posterImages  = posterImageRepository.findAllByPopupId(popup);
            List<String> imageList = new ArrayList<>();
            if (!posterImages.isEmpty())
            {
                for(PosterImage posterImage : posterImages){
                    imageList.add(posterImage.getPosterUrl());
                }
            }else{
                imageList.add(null);
            }

            ReviewListDto reviewListDto = ReviewListDto.fromEntity(review.getId(), popup.getId(), popup.getName(), review.getIsCertificated(),review.getCreatedAt(),imageList);
            reviewListDtoList.add(reviewListDto);
        }
        return reviewListDtoList;
    }
}
