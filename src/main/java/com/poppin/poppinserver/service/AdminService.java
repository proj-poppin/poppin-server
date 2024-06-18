package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.faq.request.FaqRequestDto;
import com.poppin.poppinserver.dto.faq.response.FaqResponseDto;
import com.poppin.poppinserver.dto.report.response.ReportedPopupListResponseDto;
import com.poppin.poppinserver.dto.report.response.ReportedReviewListResponseDto;
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

    public UserListDto readUsers(Long page, Long size) {
        Pageable pageable = PageRequest.of(page.intValue() - 1, size.intValue());
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

    public List<UserReviewDto> readUserReviews(Long userId, Long page, Long size, Boolean hidden) {
        Pageable pageable = PageRequest.of(page.intValue() - 1, size.intValue());
        Page<Review> reviewPage;
        if (hidden) {
            reviewPage = reviewRepository.findByUserIdAndIsVisibleOrderByCreatedAtDesc(userId, pageable, true);
        } else {
            reviewPage = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
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
                            .hiddenReview(userReview.getIsVisible())
                            .imageUrl(reviewImageListUrl)
                            .visitedAt(visitDate.isPresent() ? visitDate.get().getCreatedAt().toString() : "")
                            .build();
                    return userReviewDto;
                })
                .collect(Collectors.toList());
        return userReviewDtos;
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

    public UserListDto readSpecialCareUsers(Long page, Long size) {
        Pageable pageable = PageRequest.of(page.intValue() - 1, size.intValue());
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

    public List<ReportedReviewListResponseDto> readReviewReports(Long page, Long size, Boolean isExec) {
        Pageable pageable = PageRequest.of(page.intValue() - 1, size.intValue());
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
        return reportedReviewListResponseDtos;
    }

    public List<ReportedPopupListResponseDto> readPopupReports(Long page, Long size, Boolean isExec) {
        Pageable pageable = PageRequest.of(page.intValue() - 1, size.intValue());
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
        return reportedPopupListResponseDtos;
    }
}
