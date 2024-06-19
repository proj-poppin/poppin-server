package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.common.PageInfoDto;
import com.poppin.poppinserver.dto.common.PagingResponseDto;
import com.poppin.poppinserver.dto.faq.request.FaqRequestDto;
import com.poppin.poppinserver.dto.faq.response.FaqResponseDto;
import com.poppin.poppinserver.dto.report.response.*;
import com.poppin.poppinserver.dto.user.response.UserAdministrationDetailDto;
import com.poppin.poppinserver.dto.user.response.UserAdministrationDto;
import com.poppin.poppinserver.dto.user.response.UserListDto;
import com.poppin.poppinserver.dto.user.response.UserReviewDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {
    private final FreqQuestionRepository freqQuestionRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReportReviewRepository reportReviewRepository;
    private final ReportPopupRepository reportPopupRepository;
    private final VisitRepository visitRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final PopupRepository popupRepository;

    public List<FaqResponseDto> readFAQs() {
        List<FreqQuestion> freqQuestionList = freqQuestionRepository.findAllByOrderByCreatedAtDesc();
        List<FaqResponseDto> faqDtoList = new ArrayList<>();
        for (FreqQuestion freqQuestion : freqQuestionList) {
            faqDtoList.add(FaqResponseDto.builder()
                    .id(freqQuestion.getId())
                    .question(freqQuestion.getQuestion())
                    .answer(freqQuestion.getAnswer())
                    .createdAt(freqQuestion.getCreatedAt().toString())
                    .build());
        }
        return faqDtoList;
    }

    public FaqResponseDto createFAQ(Long adminId, FaqRequestDto faqRequestDto) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        FreqQuestion freqQuestion = FreqQuestion.builder()
                .adminId(admin)
                .question(faqRequestDto.question())
                .answer(faqRequestDto.answer())
                .createdAt(LocalDateTime.now())
                .build();
        freqQuestionRepository.save(freqQuestion);
        return FaqResponseDto.builder()
                .id(freqQuestion.getId())
                .question(freqQuestion.getQuestion())
                .answer(freqQuestion.getAnswer())
                .createdAt(freqQuestion.getCreatedAt().toString())
                .build();
    }

    public void deleteFAQ(Long faqId) {
        FreqQuestion freqQuestion = freqQuestionRepository.findById(faqId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));
        freqQuestionRepository.delete(freqQuestion);
    }

    public UserListDto readUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAllByOrderByNicknameAsc(pageable);

        List<UserAdministrationDto> userAdministrationDtoList = userPage.getContent().stream()
                .map(user -> UserAdministrationDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .requiresSpecialCare(user.getRequiresSpecialCare())
                        .build())
                .collect(Collectors.toList());
        Long userCnt = userPage.getTotalElements();

        return UserListDto.builder()
                .userList(userAdministrationDtoList)
                .userCnt(userCnt)
                .build();
    }

    public UserAdministrationDetailDto readUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Long hiddenReviewCount = reviewRepository.countByUserIdAndIsVisibleFalse(userId);
        return UserAdministrationDetailDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userImageUrl(user.getProfileImageUrl())
                .nickname(user.getNickname())
                .provider(user.getProvider())
                .birthDate(user.getBirthDate())
                .requiresSpecialCare(user.getRequiresSpecialCare())
                .hiddenReviewCount(hiddenReviewCount)
                .build();
    }

    public PagingResponseDto readUserReviews(Long userId, int page, int size, Boolean hidden) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage;
        if (hidden) {
            reviewPage = reviewRepository.findByUserIdAndIsVisibleOrderByCreatedAtDesc(userId, pageable, false);    // visible = false인 후기만
        } else {
            reviewPage = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);   // 모든 후기
        }

        List<UserReviewDto> userReviewDtos = reviewPage.getContent().stream()
                .map(userReview -> {
                    List<String> reviewImageListUrl = reviewImageRepository.findUrlAllByReviewId(userReview.getId());
                    Optional<Visit> visitDate = visitRepository.findByUserId(userId, userReview.getPopup().getId());

                    UserReviewDto userReviewDto = UserReviewDto.builder()
                            .reviewId(userReview.getId())
                            .popupName(userReview.getPopup().getName())
                            .createdAt(userReview.getCreatedAt().toString())
                            .content(userReview.getText())
                            .visible(userReview.getIsVisible())
                            .imageUrl(reviewImageListUrl)
                            .visitedAt(visitDate.isPresent() ? visitDate.get().getCreatedAt().toString() : "")
                            .build();
                    return userReviewDto;
                })
                .collect(Collectors.toList());

        PageInfoDto pageInfoDto = PageInfoDto.fromPageInfo(reviewPage);

        return PagingResponseDto.fromEntityAndPageInfo(userReviewDtos, pageInfoDto);
    }

    public UserListDto searchUsers(String text) {
        List<User> userList = userRepository.findByNicknameContainingOrEmailContainingOrderByNickname(text, text);
        List<UserAdministrationDto> userAdministrationDtoList = new ArrayList<>();
        for (User user : userList) {
            userAdministrationDtoList.add(UserAdministrationDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .requiresSpecialCare(user.getRequiresSpecialCare())
                    .build());
        }
        Long userCnt = Long.valueOf(userAdministrationDtoList.size());
        return UserListDto.builder()
                .userList(userAdministrationDtoList)
                .userCnt(userCnt)
                .build();
    }

    public UserListDto readSpecialCareUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findByRequiresSpecialCareOrderByNicknameAsc(true, pageable);

        List<UserAdministrationDto> userAdministrationDtoList = userPage.getContent().stream()
                .map(user -> UserAdministrationDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .requiresSpecialCare(user.getRequiresSpecialCare())
                        .build())
                .collect(Collectors.toList());
        Long userCnt = userPage.getTotalElements();
        return UserListDto.builder()
                .userList(userAdministrationDtoList)
                .userCnt(userCnt)
                .build();
    }

    public PagingResponseDto readReviewReports(int page, int size, Boolean isExec) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReportReview> reportReviews = reportReviewRepository.findAllByOrderByReportedAtDesc(pageable, isExec);

        List<ReportedReviewListResponseDto> reportedReviewListResponseDtos = reportReviews.getContent().stream()
                .map(reportReview -> ReportedReviewListResponseDto.builder()
                        .reportedReviewId(reportReview.getId())
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

    public PagingResponseDto readPopupReports(int page, int size, Boolean isExec) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReportPopup> reportPopups = reportPopupRepository.findAllByOrderByReportedAtDesc(pageable, isExec);

        List<ReportedPopupListResponseDto> reportedPopupListResponseDtos = reportPopups.getContent().stream()
                .map(reportPopup -> ReportedPopupListResponseDto.builder()
                        .reportedPopupId(reportPopup.getId())
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

    public ReportedPopupInfoDto readPopupReportDetail(Long popupId) {
        ReportPopup reportPopup = reportPopupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));
        ReportedPopupDetailDto reportedPopupDetailDto = ReportedPopupDetailDto.builder()
                .popupId(reportPopup.getPopupId().getId())
                .popupName(reportPopup.getPopupId().getName())
                .posterUrl(popup.getPosterUrl())
                .homepageLink(popup.getHomepageLink())
                .address(popup.getAddress())
                .addressDetail(popup.getAddressDetail())
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

    public ReportedReviewInfoDto readReviewReportDetail(Long reviewId) {
        ReportReview reportReview = reportReviewRepository.findById(reviewId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));
        Popup popup = popupRepository.findById(review.getPopup().getId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));
        ReportedReviewDetailDto reportedReviewDetailDto = ReportedReviewDetailDto.builder()
                .reviewId(review.getId())
                .reviewWriter(review.getUser().getNickname())
                .reviewCnt(review.getUser().getReviewCnt())
                .reviewContent(review.getText())
                .reviewCreatedAt(review.getCreatedAt().toString())
                .isCertificated(review.getIsCertificated())
                .imageUrl(reviewImageRepository.findUrlAllByReviewId(review.getId()))
                .build();
        ReportedPopupDetailDto reportedPopupDetailDto = ReportedPopupDetailDto.builder()
                .popupId(reportReview.getReviewId().getPopup().getId())
                .popupName(reportReview.getReviewId().getPopup().getName())
                .posterUrl(popup.getPosterUrl())
                .homepageLink(popup.getHomepageLink())
                .address(popup.getAddress())
                .addressDetail(popup.getAddressDetail())
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

    public String processPopupReport(Long adminId, Long popupId, String content) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        ReportPopup reportPopup = reportPopupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));
        reportPopup.execute(true, admin, LocalDateTime.now(), content);
        reportPopupRepository.save(reportPopup);
        return content;
    }

    public String processReviewReport(Long adminId, Long reviewId, String content) {    // 리뷰 신고 처리
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        ReportReview reportReview = reportReviewRepository.findById(reviewId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));
        reportReview.execute(true, admin, LocalDateTime.now(), content);
        reportReviewRepository.save(reportReview);
        return content;
    }
}
