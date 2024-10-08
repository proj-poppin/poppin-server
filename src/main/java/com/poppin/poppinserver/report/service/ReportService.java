package com.poppin.poppinserver.report.service;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.report.domain.ReportPopup;
import com.poppin.poppinserver.report.domain.ReportReview;
import com.poppin.poppinserver.report.dto.report.request.CreatePopupReportDto;
import com.poppin.poppinserver.report.dto.report.request.CreateReviewReportDto;
import com.poppin.poppinserver.report.repository.ReportPopupRepository;
import com.poppin.poppinserver.report.repository.ReportReviewRepository;
import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.review.repository.ReviewRepository;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReportReviewRepository reportReviewRepository;
    private final ReportPopupRepository reportPopupRepository;
    private final PopupRepository popupRepository;

    public void createReviewReport(Long userId, CreateReviewReportDto createReviewReportDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Review review = reviewRepository.findById(Long.valueOf(createReviewReportDto.reviewId()))
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

    public void createPopupReport(Long userId, CreatePopupReportDto createPopupReportDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(Long.valueOf(createPopupReportDto.popupId()))
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
