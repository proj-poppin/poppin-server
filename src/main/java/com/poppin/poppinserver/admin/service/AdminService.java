package com.poppin.poppinserver.admin.service;

import com.poppin.poppinserver.admin.dto.response.AdminInfoResponseDto;
import com.poppin.poppinserver.admin.dto.response.UserAdministrationDetailResponseDto;
import com.poppin.poppinserver.admin.dto.response.UserAdministrationListResponseDto;
import com.poppin.poppinserver.admin.dto.response.UserAdministrationResponseDto;
import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.domain.InformAlarm;
import com.poppin.poppinserver.alarm.domain.InformAlarmImage;
import com.poppin.poppinserver.alarm.dto.alarm.request.InformAlarmCreateRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.response.InformApplyResponseDto;
import com.poppin.poppinserver.alarm.repository.InformAlarmImageRepository;
import com.poppin.poppinserver.alarm.usecase.AlarmCommandUseCase;
import com.poppin.poppinserver.alarm.usecase.SendAlarmCommandUseCase;
import com.poppin.poppinserver.alarm.usecase.TokenQueryUseCase;
import com.poppin.poppinserver.core.dto.PageInfoDto;
import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.popup.service.S3Service;
import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.review.repository.ReviewQueryRepository;
import com.poppin.poppinserver.review.usecase.ReviewImageQueryUseCase;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.dto.user.response.UserReviewDto;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import com.poppin.poppinserver.visit.domain.Visit;
import com.poppin.poppinserver.visit.usecase.VisitQueryUseCase;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {
    private final UserQueryRepository userQueryRepository;

    private final ReviewQueryRepository reviewRepository;
    private final ReviewImageQueryUseCase reviewImageQueryUseCase;

    private final InformAlarmImageRepository informAlarmImageRepository;
    private final S3Service s3Service;

    private final TokenQueryUseCase tokenQueryUseCase;
    private final AlarmCommandUseCase alarmCommandUseCase;
    private final SendAlarmCommandUseCase sendAlarmCommandUseCase;

    private final VisitQueryUseCase visitQueryUseCase;

    public UserAdministrationListResponseDto readUsers(int page, int size, boolean care) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage;
        if (care) {
            userPage = userQueryRepository.findByRequiresSpecialCareOrderByNicknameAsc(true, pageable);
        } else {
            userPage = userQueryRepository.findAllByOrderByNicknameAsc(pageable);
        }

        List<UserAdministrationResponseDto> userAdministrationResponseDtoList = userPage.getContent().stream()
                .map(user -> UserAdministrationResponseDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .requiresSpecialCare(user.getRequiresSpecialCare())
                        .build())
                .collect(Collectors.toList());
        Long userCnt = userPage.getTotalElements();

        return UserAdministrationListResponseDto.builder()
                .userList(userAdministrationResponseDtoList)
                .userCnt(userCnt)
                .build();
    }

    public UserAdministrationDetailResponseDto readUserDetail(Long userId) {
        User user = userQueryRepository.findByUserId(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        UserAdministrationDetailResponseDto userAdministrationDetailResponseDto;

        if (user.getIsDeleted() && user.getDeletedAt() != null) {
            userAdministrationDetailResponseDto = createDeletedUserDetailDto(user);
        } else {
            Long hiddenReviewCount = reviewRepository.countByUserIdAndIsVisibleFalse(userId);
            userAdministrationDetailResponseDto = createActiveUserDetailDto(user, hiddenReviewCount);
        }

        return userAdministrationDetailResponseDto;
    }

    private UserAdministrationDetailResponseDto createActiveUserDetailDto(User user, Long hiddenReviewCount) {
        return UserAdministrationDetailResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userImageUrl(user.getProfileImageUrl())
                .nickname(user.getNickname())
                .provider(user.getProvider())
                .requiresSpecialCare(user.getRequiresSpecialCare())
                .hiddenReviewCount(hiddenReviewCount)
                .build();
    }

    private UserAdministrationDetailResponseDto createDeletedUserDetailDto(User user) {
        return UserAdministrationDetailResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userImageUrl(null)
                .nickname("탈퇴한 유저입니다.")
                .provider(null)
                .requiresSpecialCare(false)
                .hiddenReviewCount(0L)
                .build();
    }

    public UserAdministrationListResponseDto searchUsers(String text) {
        List<User> userList = userQueryRepository.findByNicknameContainingOrEmailContainingOrderByNickname(text, text);
        List<UserAdministrationResponseDto> userAdministrationResponseDtoList = new ArrayList<>();
        for (User user : userList) {
            userAdministrationResponseDtoList.add(UserAdministrationResponseDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .requiresSpecialCare(user.getRequiresSpecialCare())
                    .build());
        }
        Long userCnt = (long) userAdministrationResponseDtoList.size();
        return UserAdministrationListResponseDto.builder()
                .userList(userAdministrationResponseDtoList)
                .userCnt(userCnt)
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

    public AdminInfoResponseDto readAdminInfo(Long adminId) {
        User admin = userQueryRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        return AdminInfoResponseDto.fromEntity(admin);
    }
}
