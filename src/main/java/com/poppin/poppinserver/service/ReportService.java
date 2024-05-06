package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.report.request.CreatePopupReportDto;
import com.poppin.poppinserver.dto.report.request.CreateReviewReportDto;
import com.poppin.poppinserver.dto.review.request.ReviewInfoDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReportReviewRepository reportReviewRepository;
    private final ReportPopupRepository reportPopupRepository;
    private final PopupRepository popupRepository;

    public Review hideReview(Long userId, ReviewInfoDto reviewInfoDto){


        /*관리자 여부 체크 메서드도 필요*/
        /**/

        Review review = reviewRepository.findById(reviewInfoDto.reviewId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_REVIEW));

        review.updateReviewVisible();

        return reviewRepository.save(review);

    }

    public void createReviewReport(Long userId, Long reviewId, CreateReviewReportDto createReviewReportDto){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_REVIEW));
        ReportReview reportReview = ReportReview.builder()
                .reporterId(user)
                .reviewId(review)
                .reportedAt(LocalDateTime.now())
                .reportContent(createReviewReportDto.content())
                .isExecuted(false)
                .build();
        reportReviewRepository.save(reportReview);
    }

    public void createPopupReport(Long userId, Long popupId, CreatePopupReportDto createPopupReportDto){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));
        ReportPopup reportPopup = ReportPopup.builder()
                .reporterId(user)
                .popupId(popup)
                .reportedAt(LocalDateTime.now())
                .reportContent(createPopupReportDto.content())
                .isExecuted(false)
                .build();
        reportPopupRepository.save(reportPopup);
    }
}
