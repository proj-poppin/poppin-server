package com.poppin.poppinserver.admin.service;

import com.poppin.poppinserver.core.dto.PageInfoDto;
import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.popup.usecase.PopupQueryUseCase;
import com.poppin.poppinserver.report.domain.ReportPopup;
import com.poppin.poppinserver.report.domain.ReportReview;
import com.poppin.poppinserver.report.dto.report.request.CreateReportExecContentDto;
import com.poppin.poppinserver.report.dto.report.response.ReportContentDto;
import com.poppin.poppinserver.report.dto.report.response.ReportExecContentResponseDto;
import com.poppin.poppinserver.report.dto.report.response.ReportedPopupDetailDto;
import com.poppin.poppinserver.report.dto.report.response.ReportedPopupInfoDto;
import com.poppin.poppinserver.report.dto.report.response.ReportedPopupListResponseDto;
import com.poppin.poppinserver.report.dto.report.response.ReportedReviewDetailDto;
import com.poppin.poppinserver.report.dto.report.response.ReportedReviewInfoDto;
import com.poppin.poppinserver.report.dto.report.response.ReportedReviewListResponseDto;
import com.poppin.poppinserver.report.repository.ReportPopupRepository;
import com.poppin.poppinserver.report.repository.ReportReviewRepository;
import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.review.repository.ReviewQueryRepository;
import com.poppin.poppinserver.review.usecase.ReviewImageQueryUseCase;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminReportService {
    private final ReportReviewRepository reportReviewRepository;
    private final ReportPopupRepository reportPopupRepository;
    private final UserQueryRepository userQueryRepository;
    private final ReviewQueryRepository reviewRepository;
    private final ReviewImageQueryUseCase reviewImageQueryUseCase;
    private final PopupQueryUseCase popupQueryUseCase;

    @Transactional(readOnly = true)
    public PagingResponseDto<List<ReportedReviewListResponseDto>> readReviewReports(int page, int size,
                                                                                    Boolean isExec) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReportReview> reportReviews = reportReviewRepository.findAllByOrderByReportedAtDesc(pageable, isExec);

        List<ReportedReviewListResponseDto> reportedReviewListResponseDtos = reportReviews.getContent().stream()
                .map(reportReview -> ReportedReviewListResponseDto.builder()
                        .reportId(reportReview.getId())
                        .reviewId(reportReview.getReviewId().getId())
                        .reporter(reportReview.getReporterId().getNickname())
                        .popupName(reportReview.getReviewId().getPopup().getName())
                        .executed(reportReview.getIsExecuted())
                        .reportedAt(reportReview.getReportedAt().toString())
                        .build())
                .collect(Collectors.toList());
        PageInfoDto pageInfoDto = PageInfoDto.fromPageInfo(reportReviews);
        return PagingResponseDto.fromEntityAndPageInfo(reportedReviewListResponseDtos, pageInfoDto);
    }

    @Transactional(readOnly = true)
    public PagingResponseDto<List<ReportedPopupListResponseDto>> readPopupReports(int page, int size, Boolean isExec) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReportPopup> reportPopups = reportPopupRepository.findAllByOrderByReportedAtDesc(pageable, isExec);

        List<ReportedPopupListResponseDto> reportedPopupListResponseDtos = reportPopups.getContent().stream()
                .map(reportPopup -> ReportedPopupListResponseDto.builder()
                        .reportId(reportPopup.getId())
                        .popupId(reportPopup.getPopupId().getId())
                        .reporter(reportPopup.getReporterId().getNickname())
                        .popupName(reportPopup.getPopupId().getName())
                        .executed(reportPopup.getIsExecuted())
                        .reportedAt(reportPopup.getReportedAt().toString())
                        .build())
                .collect(Collectors.toList());
        PageInfoDto pageInfoDto = PageInfoDto.fromPageInfo(reportPopups);
        return PagingResponseDto.fromEntityAndPageInfo(reportedPopupListResponseDtos, pageInfoDto);
    }

    @Transactional(readOnly = true)
    public ReportedPopupInfoDto readPopupReportDetail(Long reportId) {
        ReportPopup reportPopup = reportPopupRepository.findById(reportId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));
        Popup popup = popupQueryUseCase.findPopupById(reportPopup.getPopupId().getId());

        ReportedPopupDetailDto reportedPopupDetailDto = ReportedPopupDetailDto.builder()
                .popupId(popup.getId())
                .popupName(popup.getName())
                .introduce(popup.getIntroduce())
                .posterUrl(popup.getPosterUrl())
                .homepageLink(popup.getHomepageLink())
                .address(popup.getAddress())
                .addressDetail(popup.getAddressDetail())
                .openDate(popup.getOpenDate().toString())
                .closeDate(popup.getCloseDate().toString())
                .openTime(popup.getOpenTime().toString())
                .closeTime(popup.getCloseTime().toString())
                .entranceFee(popup.getEntranceFee())
                .availableAge(popup.getAvailableAge().getAvailableAgeProvider())
                .parkingAvailable(popup.getParkingAvailable())
                .resvRequired(popup.getResvRequired())
                .build();
        ReportContentDto reportContentDto = ReportContentDto.builder()
                .reportId(reportPopup.getId())
                .reporter(reportPopup.getReporterId().getNickname())
                .reportedAt(reportPopup.getReportedAt().toString())
                .content(reportPopup.getReportContent())
                .build();
        return ReportedPopupInfoDto.builder()
                .reportedPopupDetailDto(reportedPopupDetailDto)
                .reportContentDto(reportContentDto)
                .build();
    }

    @Transactional(readOnly = true)
    public ReportedReviewInfoDto readReviewReportDetail(Long reportId) {
        ReportReview reportReview = reportReviewRepository.findById(reportId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));
        Review review = reviewRepository.findById(reportReview.getReviewId().getId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));
        Popup popup = popupRepository.findById(review.getPopup().getId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));
        ReportedReviewDetailDto reportedReviewDetailDto = ReportedReviewDetailDto.builder()
                .reviewId(review.getId())
                .reviewWriter(review.getUser().getNickname())
                .reviewCnt(review.getUser().getReviewCnt())
                .reviewContent(review.getText())
                .reviewCreatedAt(review.getCreatedAt().toString())
                .isCertificated(review.getIsCertified())
                .imageUrl(reviewImageQueryUseCase.findUrlAllByReviewId(review.getId()))
                .userProfileImageUrl(review.getUser().getProfileImageUrl())
                .build();
        ReportedPopupDetailDto reportedPopupDetailDto = ReportedPopupDetailDto.builder()
                .popupId(reportReview.getReviewId().getPopup().getId())
                .popupName(reportReview.getReviewId().getPopup().getName())
                .posterUrl(popup.getPosterUrl())
                .homepageLink(popup.getHomepageLink())
                .introduce(popup.getIntroduce())
                .address(popup.getAddress())
                .addressDetail(popup.getAddressDetail())
                .openDate(popup.getOpenDate().toString())
                .closeDate(popup.getCloseDate().toString())
                .openTime(popup.getOpenTime().toString())
                .closeTime(popup.getCloseTime().toString())
                .entranceFee(popup.getEntranceFee())
                .availableAge(popup.getAvailableAge().getAvailableAgeProvider())
                .parkingAvailable(popup.getParkingAvailable())
                .resvRequired(popup.getResvRequired())
                .build();
        ReportContentDto reportContentDto = ReportContentDto.builder()
                .reportId(reportReview.getId())
                .reporter(reportReview.getReporterId().getNickname())
                .reportedAt(reportReview.getReportedAt().toString())
                .content(reportReview.getReportContent())
                .build();
        return ReportedReviewInfoDto.builder()
                .reportedPopupDetailDto(reportedPopupDetailDto)
                .reportedReviewDetailDto(reportedReviewDetailDto)
                .reportContentDto(reportContentDto)
                .build();
    }

    @Transactional
    public void processPopupReport(Long adminId, Long reportId, CreateReportExecContentDto createReportExecContentDto) {
        User admin = userQueryRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        ReportPopup reportPopup = reportPopupRepository.findById(reportId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP_REPORT));
        reportPopup.execute(true, admin, LocalDateTime.now(), createReportExecContentDto.content());
        reportPopupRepository.save(reportPopup);
    }

    // 후기 신고 처리 생성 -> 후기 가리기
    @Transactional
    public void processReviewReport(Long adminId, Long reportId,
                                    CreateReportExecContentDto createReportExecContentDto) {
        User admin = userQueryRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        ReportReview reportReview = reportReviewRepository.findById(reportId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_REVIEW_REPORT));
        Review review = reviewRepository.findById(reportReview.getReviewId().getId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_REVIEW));

        User reviewAuthor = review.getUser();
        if (review.getIsVisible()) { // 가려진 후기가 아닌 경우에만(즉 최초 신고 시에만) 신고 횟수 증가
            reviewAuthor.addReportCnt();
        }

        review.updateReviewInvisible(); // 후기 가리고
        reviewRepository.save(review);

        if (reviewAuthor.getReportedCnt() >= 3) {    // 신고 횟수 3회 이상 시 특별 관리 대상으로 변경
            reviewAuthor.requiresSpecialCare();
        }
        userQueryRepository.save(reviewAuthor);

        reportReview.execute(true, admin, LocalDateTime.now(), createReportExecContentDto.content());
        reportReviewRepository.save(reportReview);
    }

    public void processReviewReportExec(Long adminId, Long reportId) {
        User admin = userQueryRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        ReportReview reportReview = reportReviewRepository.findById(reportId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));
        reportReview.execute(true, admin, LocalDateTime.now(), null);
        reportReviewRepository.save(reportReview);
    }

    @Transactional(readOnly = true)
    public ReportExecContentResponseDto readPopupReportExecContent(Long reportId) {
        ReportPopup reportPopup = reportPopupRepository.findById(reportId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP_REPORT));
        return ReportExecContentResponseDto.builder()
                .reportId(reportPopup.getId())
                .adminName(reportPopup.getAdminId().getNickname())
                .executedAt(reportPopup.getExecutedAt().toString())
                .content(reportPopup.getExecuteContent())
                .build();
    }

    @Transactional(readOnly = true)
    public ReportExecContentResponseDto readReviewReportExecContent(Long reportId) {
        ReportReview reportReview = reportReviewRepository.findById(reportId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_REVIEW_REPORT));
        return ReportExecContentResponseDto.builder()
                .reportId(reportReview.getId())
                .adminName(reportReview.getAdminId().getNickname())
                .executedAt(reportReview.getExecutedAt().toString())
                .content(reportReview.getExecuteContent())
                .build();
    }
}
