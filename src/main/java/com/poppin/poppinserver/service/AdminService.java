package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.alarm.request.InformAlarmCreateRequestDto;
import com.poppin.poppinserver.dto.alarm.response.InformApplyResponseDto;
import com.poppin.poppinserver.dto.common.PageInfoDto;
import com.poppin.poppinserver.dto.common.PagingResponseDto;
import com.poppin.poppinserver.dto.faq.request.FaqRequestDto;
import com.poppin.poppinserver.dto.faq.response.FaqResponseDto;
import com.poppin.poppinserver.dto.report.request.CreateReportExecContentDto;
import com.poppin.poppinserver.dto.report.response.*;
import com.poppin.poppinserver.dto.user.response.UserAdministrationDetailDto;
import com.poppin.poppinserver.dto.user.response.UserAdministrationDto;
import com.poppin.poppinserver.dto.user.response.UserListDto;
import com.poppin.poppinserver.dto.user.response.UserReviewDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final FCMTokenRepository fcmTokenRepository;
    private final InformAlarmRepository informAlarmRepository;
    private final InformAlarmImageRepository informAlarmImageRepository;
    private final S3Service s3Service;
    private final AlarmService alarmService;
    private final AlarmListService alarmListService;
    private final FCMSendService fcmSendService;

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

    public UserListDto readUsers(int page, int size, boolean care) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage;
        if (care) {
            userPage = userRepository.findByRequiresSpecialCareOrderByNicknameAsc(true, pageable);
        } else {
            userPage = userRepository.findAllByOrderByNicknameAsc(pageable);
        }

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
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        UserAdministrationDetailDto userAdministrationDetailDto;

        if (user.getIsDeleted() && user.getDeletedAt() != null) {
            userAdministrationDetailDto = createDeletedUserDetailDto(user);
        } else {
            Long hiddenReviewCount = reviewRepository.countByUserIdAndIsVisibleFalse(userId);
            userAdministrationDetailDto = createActiveUserDetailDto(user, hiddenReviewCount);
        }

        return userAdministrationDetailDto;
    }

    private UserAdministrationDetailDto createActiveUserDetailDto(User user, Long hiddenReviewCount) {
        return UserAdministrationDetailDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userImageUrl(user.getProfileImageUrl())
                .nickname(user.getNickname())
                .provider(user.getProvider())
                .requiresSpecialCare(user.getRequiresSpecialCare())
                .hiddenReviewCount(hiddenReviewCount)
                .build();
    }

    private UserAdministrationDetailDto createDeletedUserDetailDto(User user) {
        return UserAdministrationDetailDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userImageUrl(null)
                .nickname("탈퇴한 유저입니다.")
                .provider(null)
                .requiresSpecialCare(false)
                .hiddenReviewCount(0L)
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

    @Transactional(readOnly = true)
    public PagingResponseDto readReviewReports(int page, int size, Boolean isExec) {
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
    public PagingResponseDto readPopupReports(int page, int size, Boolean isExec) {
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
        Popup popup = popupRepository.findById(reportPopup.getPopupId().getId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));
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
                .isCertificated(review.getIsCertificated())
                .imageUrl(reviewImageRepository.findUrlAllByReviewId(review.getId()))
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
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        ReportPopup reportPopup = reportPopupRepository.findById(reportId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP_REPORT));
        reportPopup.execute(true, admin, LocalDateTime.now(), createReportExecContentDto.content());
        reportPopupRepository.save(reportPopup);
    }

    // 후기 신고 처리 생성 -> 후기 가리기
    @Transactional
    public void processReviewReport(Long adminId, Long reportId, CreateReportExecContentDto createReportExecContentDto) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        ReportReview reportReview = reportReviewRepository.findById(reportId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_REVIEW_REPORT));
        Review review = reviewRepository.findById(reportReview.getReviewId().getId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_REVIEW));

        User reviewAuthor = review.getUser();
        if (review.getIsVisible() == true){ // 가려진 후기가 아닌 경우에만(즉 최초 신고 시에만) 신고 횟수 증가
            reviewAuthor.addReportCnt();
        }

        review.updateReviewInvisible(); // 후기 가리고
        reviewRepository.save(review);

        if (reviewAuthor.getReportedCnt() >= 3){    // 신고 횟수 3회 이상 시 특별 관리 대상으로 변경
            reviewAuthor.requiresSpecialCare();
        }
        userRepository.save(reviewAuthor);

        reportReview.execute(true, admin, LocalDateTime.now(), createReportExecContentDto.content());
        reportReviewRepository.save(reportReview);
    }


    public InformApplyResponseDto createInformation(
            Long adminId,
            InformAlarmCreateRequestDto requestDto,
            MultipartFile images
    ){
        // 관리자 여부 확인
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        try {

            // Alarm 객체 저장
            log.info("INFORM ALARM Entity Saving");

            InformAlarm informAlarm = alarmService.insertInformAlarm(requestDto);
            if (informAlarm.equals(null))throw new CommonException(ErrorCode.INFO_ALARM_ERROR);
            else informAlarmRepository.save(informAlarm);

            // Inform 읽음 여부 테이블에 fcm 토큰 정보와 함께 저장
            List<FCMToken> tokenList = fcmTokenRepository.findAll();
            for (FCMToken token: tokenList){
                alarmListService.insertInformIsRead(token, informAlarm);
            }

            // 이미지 저장
            List<String> fileUrls = s3Service.uploadInformationPoster(images);

            List<InformAlarmImage> informAlarmImages = new ArrayList<>();
            for(String url : fileUrls){
                InformAlarmImage informAlarmImage = InformAlarmImage.builder()
                        .informAlarm(informAlarm)
                        .posterUrl(url)
                        .build();
                informAlarmImages.add(informAlarmImage);
            }
            informAlarmImageRepository.saveAll(informAlarmImages);

            // 저장 성공
            if (informAlarm != null){
                // 앱 푸시 발송
                fcmSendService.sendInformationByFCMToken(tokenList, requestDto , informAlarm);
                // 푸시 성공
                InformApplyResponseDto informApplyResponseDto = InformApplyResponseDto.fromEntity(informAlarm, fileUrls);
                return informApplyResponseDto; // 최종 성공 반환
            }
        // InformAlarm 객체 저장 실패
        }catch (Exception e){
            e.printStackTrace();
            log.error("INFORM ALARM ERROR : " + e.getMessage());
        }
        return null;
    }

    public void processReviewReportExec(Long adminId, Long reportId) {
        User admin = userRepository.findById(adminId)
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
