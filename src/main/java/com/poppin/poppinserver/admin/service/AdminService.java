package com.poppin.poppinserver.admin.service;

import com.poppin.poppinserver.admin.dto.request.AdminFaqRequestDto;
import com.poppin.poppinserver.admin.dto.response.AdminFaqResponseDto;
import com.poppin.poppinserver.admin.dto.response.AdminInfoResponseDto;
import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.domain.InformAlarm;
import com.poppin.poppinserver.alarm.domain.InformAlarmImage;
import com.poppin.poppinserver.alarm.dto.alarm.request.InformAlarmCreateRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.response.InformApplyResponseDto;
import com.poppin.poppinserver.alarm.repository.InformAlarmImageRepository;
import com.poppin.poppinserver.alarm.usecase.AlarmCommandUseCase;
import com.poppin.poppinserver.alarm.usecase.AlarmListQueryUseCase;
import com.poppin.poppinserver.alarm.usecase.SendAlarmCommandUseCase;
import com.poppin.poppinserver.alarm.usecase.TokenQueryUseCase;
import com.poppin.poppinserver.core.constant.Constant;
import com.poppin.poppinserver.core.dto.PageInfoDto;
import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.util.HeaderUtil;
import com.poppin.poppinserver.core.util.JwtUtil;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.popup.service.S3Service;
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
import com.poppin.poppinserver.user.domain.FreqQuestion;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.domain.type.EUserRole;
import com.poppin.poppinserver.user.dto.auth.response.JwtTokenDto;
import com.poppin.poppinserver.user.dto.user.response.UserAdministrationDetailDto;
import com.poppin.poppinserver.user.dto.user.response.UserAdministrationDto;
import com.poppin.poppinserver.user.dto.user.response.UserListDto;
import com.poppin.poppinserver.user.dto.user.response.UserReviewDto;
import com.poppin.poppinserver.user.repository.FreqQuestionRepository;
import com.poppin.poppinserver.user.repository.UserCommandRepository;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import com.poppin.poppinserver.visit.domain.Visit;
import com.poppin.poppinserver.visit.usecase.VisitQueryUseCase;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {
    private final FreqQuestionRepository freqQuestionRepository;
    private final UserQueryRepository userQueryRepository;
    private final ReviewQueryRepository reviewRepository;
    private final ReportReviewRepository reportReviewRepository;
    private final ReportPopupRepository reportPopupRepository;

    private final ReviewImageQueryUseCase reviewImageQueryUseCase;
    private final PopupRepository popupRepository;

    private final InformAlarmImageRepository informAlarmImageRepository;
    private final S3Service s3Service;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final UserCommandRepository userCommandRepository;

    private final TokenQueryUseCase tokenQueryUseCase;
    private final AlarmCommandUseCase alarmCommandUseCase;
    private final SendAlarmCommandUseCase sendAlarmCommandUseCase;
    private final AlarmListQueryUseCase alarmListQueryUseCase;
    private final VisitQueryUseCase visitQueryUseCase;

    public List<AdminFaqResponseDto> readFAQs() {
        List<FreqQuestion> freqQuestionList = freqQuestionRepository.findAllByOrderByCreatedAtDesc();
        List<AdminFaqResponseDto> faqDtoList = new ArrayList<>();
        for (FreqQuestion freqQuestion : freqQuestionList) {
            faqDtoList.add(AdminFaqResponseDto.builder()
                    .faqId(freqQuestion.getId())
                    .question(freqQuestion.getQuestion())
                    .answer(freqQuestion.getAnswer())
                    .createdAt(freqQuestion.getCreatedAt().toString())
                    .build());
        }
        return faqDtoList;
    }

    public AdminFaqResponseDto createFAQ(Long adminId, AdminFaqRequestDto adminFaqRequestDto) {
        User admin = userQueryRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        FreqQuestion freqQuestion = FreqQuestion.builder()
                .adminId(admin)
                .question(adminFaqRequestDto.question())
                .answer(adminFaqRequestDto.answer())
                .createdAt(LocalDateTime.now())
                .build();
        freqQuestionRepository.save(freqQuestion);
        return AdminFaqResponseDto.builder()
                .faqId(freqQuestion.getId())
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
            userPage = userQueryRepository.findByRequiresSpecialCareOrderByNicknameAsc(true, pageable);
        } else {
            userPage = userQueryRepository.findAllByOrderByNicknameAsc(pageable);
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
        User user = userQueryRepository.findByUserId(userId)
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

    public PagingResponseDto<List<UserReviewDto>> readUserReviews(Long userId, int page, int size, Boolean hidden) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage;
        if (hidden) {
            reviewPage = reviewRepository.findByUserIdAndIsVisibleOrderByCreatedAtDesc(userId, pageable,
                    false);    // visible = false인 후기만
        } else {
            reviewPage = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);   // 모든 후기
        }

        List<UserReviewDto> userReviewDtoList = reviewPage.getContent().stream()
                .map(userReview -> {
                    List<String> reviewImageListUrl = reviewImageQueryUseCase.findUrlAllByReviewId(userReview.getId());
                    Optional<Visit> visitDate = visitQueryUseCase.findByUserId(userId, userReview.getPopup().getId());

                    UserReviewDto userReviewDto = UserReviewDto.of(userReview.getId(), userReview.getPopup().getName(),
                            !visitDate.isEmpty() ?
                                    visitDate.get().getCreatedAt().toString() : "",
                            userReview.getCreatedAt().toString(),
                            userReview.getText(), reviewImageListUrl, userReview.getIsVisible());

                    return userReviewDto;
                })
                .collect(Collectors.toList());

        PageInfoDto pageInfoDto = PageInfoDto.fromPageInfo(reviewPage);

        return PagingResponseDto.fromEntityAndPageInfo(userReviewDtoList, pageInfoDto);
    }

    public UserListDto searchUsers(String text) {
        List<User> userList = userQueryRepository.findByNicknameContainingOrEmailContainingOrderByNickname(text, text);
        List<UserAdministrationDto> userAdministrationDtoList = new ArrayList<>();
        for (User user : userList) {
            userAdministrationDtoList.add(UserAdministrationDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .requiresSpecialCare(user.getRequiresSpecialCare())
                    .build());
        }
        Long userCnt = (long) userAdministrationDtoList.size();
        return UserListDto.builder()
                .userList(userAdministrationDtoList)
                .userCnt(userCnt)
                .build();
    }

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


    public InformApplyResponseDto createInformation(
            Long adminId,
            InformAlarmCreateRequestDto requestDto,
            MultipartFile images
    ) {
        // 관리자 여부 확인
        User admin = userQueryRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        try {

            // Alarm 객체 저장
            log.info("INFORM ALARM Entity Saving");

            InformAlarm informAlarm = alarmCommandUseCase.insertInformAlarm(requestDto);

            // Inform 읽음 여부 테이블에 fcm 토큰 정보와 함께 저장
            List<FCMToken> tokenList = tokenQueryUseCase.findAll();
            for (FCMToken token : tokenList) {
                alarmCommandUseCase.insertInformIsRead(token, informAlarm);
            }

            // 이미지 저장
            List<String> fileUrls = s3Service.uploadInformationPoster(images);

            List<InformAlarmImage> informAlarmImages = new ArrayList<>();
            for (String url : fileUrls) {
                InformAlarmImage informAlarmImage = InformAlarmImage.builder()
                        .informAlarm(informAlarm)
                        .posterUrl(url)
                        .build();
                informAlarmImages.add(informAlarmImage);
            }
            informAlarmImageRepository.saveAll(informAlarmImages);

            // 저장 성공
            if (informAlarm != null) {
                // 앱 푸시 발송
                sendAlarmCommandUseCase.sendInformationAlarm(tokenList, requestDto, informAlarm);
                // 푸시 성공
                InformApplyResponseDto informApplyResponseDto = InformApplyResponseDto.fromEntity(informAlarm,
                        fileUrls);
                return informApplyResponseDto; // 최종 성공 반환
            }
            // InformAlarm 객체 저장 실패
        } catch (Exception e) {
            e.printStackTrace();
            log.error("INFORM ALARM ERROR : " + e.getMessage());
        }
        return null;
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

    public JwtTokenDto authSignIn(String authorizationHeader) {
        String encoded = HeaderUtil.refineHeader(authorizationHeader, Constant.BASIC_PREFIX);
        String[] decoded = new String(Base64.getDecoder().decode(encoded)).split(":");
        String email = decoded[0];
        String password = decoded[1];
        User user = userQueryRepository.findByEmail(email)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        if (!user.getRole().equals(EUserRole.ADMIN)) {
            throw new CommonException(ErrorCode.ACCESS_DENIED_ERROR);
        }
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new CommonException(ErrorCode.INVALID_LOGIN);
        }

        JwtTokenDto jwtTokenDto = jwtUtil.generateToken(user.getId(), user.getRole());
        userCommandRepository.updateRefreshTokenAndLoginStatus(user.getId(), jwtTokenDto.refreshToken(), true);

        return jwtTokenDto;
    }

    @Transactional
    public JwtTokenDto refresh(@NotNull String refreshToken) {
        String token = refineToken(refreshToken);
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userQueryRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        if (!user.getRefreshToken().equals(token)) {
            throw new CommonException(ErrorCode.INVALID_TOKEN_ERROR);
        }
        JwtTokenDto jwtTokenDto = jwtUtil.generateToken(userId, user.getRole());
        userCommandRepository.updateRefreshTokenAndLoginStatus(user.getId(), jwtTokenDto.refreshToken(), true);
        return jwtTokenDto;
    }

    private String refineToken(String accessToken) {
        if (accessToken.startsWith(Constant.BEARER_PREFIX)) {
            return accessToken.substring(Constant.BEARER_PREFIX.length());
        } else {
            return accessToken;
        }
    }

    public AdminInfoResponseDto readAdminInfo(Long adminId) {
        User admin = userQueryRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        return AdminInfoResponseDto.fromEntity(admin);
    }
}
