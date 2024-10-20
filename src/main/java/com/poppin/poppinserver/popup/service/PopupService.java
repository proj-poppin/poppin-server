package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.alarm.domain.*;
import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmKeywordCreateRequestDto;
import com.poppin.poppinserver.alarm.dto.fcm.request.PushRequestDto;
import com.poppin.poppinserver.alarm.dto.informAlarm.response.NoticeDto;
import com.poppin.poppinserver.alarm.repository.*;
import com.poppin.poppinserver.alarm.service.AlarmService;
import com.poppin.poppinserver.alarm.service.FCMSendService;
import com.poppin.poppinserver.alarm.service.FCMTokenService;
import com.poppin.poppinserver.core.dto.PageInfoDto;
import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.scheduler.FCMScheduler;
import com.poppin.poppinserver.core.type.EOperationStatus;
import com.poppin.poppinserver.core.type.EPopupSort;
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.core.type.EPushInfo;
import com.poppin.poppinserver.core.util.HeaderUtil;
import com.poppin.poppinserver.core.util.PrepardSearchUtil;
import com.poppin.poppinserver.core.util.SelectRandomUtil;
import com.poppin.poppinserver.inform.repository.ManagerInformRepository;
import com.poppin.poppinserver.inform.repository.UserInformRepository;
import com.poppin.poppinserver.interest.domain.Interest;
import com.poppin.poppinserver.interest.repository.InterestRepository;
import com.poppin.poppinserver.modifyInfo.service.ModifyInfoService;
import com.poppin.poppinserver.popup.domain.*;
import com.poppin.poppinserver.popup.dto.popup.request.CreatePopupDto;
import com.poppin.poppinserver.popup.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.popup.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.popup.dto.popup.request.UpdatePopupDto;
import com.poppin.poppinserver.popup.dto.popup.response.*;
import com.poppin.poppinserver.popup.repository.*;
import com.poppin.poppinserver.popup.repository.specification.PopupSpecification;
import com.poppin.poppinserver.report.repository.ReportPopupRepository;
import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.review.domain.ReviewImage;
import com.poppin.poppinserver.review.dto.response.ReviewInfoDto;
import com.poppin.poppinserver.review.repository.ReviewImageRepository;
import com.poppin.poppinserver.review.repository.ReviewRecommendRepository;
import com.poppin.poppinserver.review.repository.ReviewRepository;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.BlockedUserRepository;
import com.poppin.poppinserver.user.repository.UserRepository;
import com.poppin.poppinserver.visit.domain.Visit;
import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataInfoDto;
import com.poppin.poppinserver.visit.repository.VisitRepository;
import com.poppin.poppinserver.visit.repository.VisitorDataRepository;
import com.poppin.poppinserver.visit.service.VisitService;
import com.poppin.poppinserver.visit.service.VisitorDataService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopupService {
    private final PopupRepository popupRepository;
    private final ReviewRepository reviewRepository;
    private final PosterImageRepository posterImageRepository;
    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final FCMTokenRepository fcmTokenRepository;
    private final ReopenDemandRepository reopenDemandRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final VisitRepository visitRepository;
    private final BlockedUserRepository blockedUserRepository;
    private final BlockedPopupRepository blockedPopupRepository;
    private final VisitorDataService visitorDataService;
    private final VisitService visitService;

    private final HeaderUtil headerUtil;

    public PopupGuestDetailDto readGuestDetail(String strPopupId) {
        Long popupId = Long.valueOf(strPopupId);

        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        popup.addViewCnt(); // 조회수 + 1

        List<Review> reviews = reviewRepository.findAllByPopupIdOrderByRecommendCntDesc(popupId);

        // 리뷰 이미지 목록 가져오기
        List<List<String>> reviewImagesList = new ArrayList<>();
        List<String> profileImagesList = new ArrayList<>();
        List<Integer> reviewCntList = new ArrayList<>();

        for (Review review : reviews) {
            List<ReviewImage> reviewImages = reviewImageRepository.findAllByReviewId(review.getId());

            List<String> imagesList = new ArrayList<>();
            for (ReviewImage reviewImage : reviewImages) {
                imagesList.add(reviewImage.getImageUrl());
            }

            reviewImagesList.add(imagesList);
            profileImagesList.add(review.getUser().getProfileImageUrl());
            reviewCntList.add(review.getUser().getReviewCnt());
        }

        List<ReviewInfoDto> reviewInfoList = ReviewInfoDto.fromEntityList(reviews, reviewImagesList, profileImagesList,
                reviewCntList);

        VisitorDataInfoDto visitorDataDto = visitorDataService.getVisitorData(popupId); // 방문자 데이터

        Optional<Integer> visitors = visitService.showRealTimeVisitors(popupId); // 실시간 방문자

        popupRepository.save(popup);

        // 이미지 목록 가져오기
        List<PosterImage> posterImages = posterImageRepository.findByPopupId(popup);

        List<String> imageList = new ArrayList<>();
        for (PosterImage posterImage : posterImages) {
            imageList.add(posterImage.getPosterUrl());
        }

        return PopupGuestDetailDto.fromEntity(popup, imageList, reviewInfoList, visitorDataDto, visitors);
    } // 비로그인 상세조회

    @Transactional
    public PopupDetailDto readDetail(String strPopupId, Long userId) {
        Long popupId = Long.valueOf(strPopupId);

        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        popup.addViewCnt(); // 조회수 + 1

        List<Review> reviews = reviewRepository.findAllByPopupIdOrderByRecommendCntDesc(popupId);

        List<Long> blockedUserIds = blockedUserRepository.findBlockedUserIdsByUserId(userId);
        log.info("Blocked User IDs: " + blockedUserIds.toString());

        // 차단된 사용자의 리뷰를 제외한 리스트
        List<Review> filteredReviews = new ArrayList<>();
        // 리뷰 이미지 목록, 프로필 이미지 가져오기
        List<List<String>> reviewImagesList = new ArrayList<>();
        List<String> profileImagesList = new ArrayList<>();
        List<Integer> reviewCntList = new ArrayList<>();

        for (Review review : reviews) {
            if (blockedUserIds.contains(review.getUser().getId())) {
                log.info("Filtered Review by User ID: " + review.getUser().getId());
                continue;
            }

            filteredReviews.add(review);

            List<ReviewImage> reviewImages = reviewImageRepository.findAllByReviewId(review.getId());

            List<String> imagesList = new ArrayList<>();
            for (ReviewImage reviewImage : reviewImages) {
                imagesList.add(reviewImage.getImageUrl());
            }

            reviewImagesList.add(imagesList);
            profileImagesList.add(review.getUser().getProfileImageUrl());
            reviewCntList.add(review.getUser().getReviewCnt());
        }

        List<ReviewInfoDto> reviewInfoList = ReviewInfoDto.fromEntityList(filteredReviews, reviewImagesList,
                profileImagesList, reviewCntList);

        VisitorDataInfoDto visitorDataDto = visitorDataService.getVisitorData(popupId); // 방문자 데이터

        Optional<Integer> visitors = visitService.showRealTimeVisitors(popupId); // 실시간 방문자

        popupRepository.save(popup);

        // 이미지 목록 가져오기
        List<PosterImage> posterImages = posterImageRepository.findAllByPopupId(popup);

        List<String> imageList = new ArrayList<>();
        for (PosterImage posterImage : posterImages) {
            imageList.add(posterImage.getPosterUrl());
        }

        // 관심 여부 확인
        Boolean isInterested = interestRepository.findByUserIdAndPopupId(userId, popupId).isPresent();

        Optional<Visit> visit = visitRepository.findByUserId(userId, popupId);

        // 차단 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Boolean isBlocked = blockedPopupRepository.findByPopupIdAndUserId(popup, user).isPresent();

        // 방문 여부 확인
        if (!visit.isEmpty()) {
            return PopupDetailDto.fromEntity(popup, imageList, isInterested, reviewInfoList, visitorDataDto, visitors,
                    true, isBlocked); // 이미 방문함
        } else {
            return PopupDetailDto.fromEntity(popup, imageList, isInterested, reviewInfoList, visitorDataDto, visitors,
                    false, isBlocked); // 방문 한적 없음
        }
    } // 로그인 상세조회

    public PopupStoreDto readPopupStore(String strPopupId, HttpServletRequest request) {
        Long popupId = Long.valueOf(strPopupId);

        Long userId = headerUtil.parseUserId(request);
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        if (userId != null) {
            return getPopupStoreDto(popup, userId);
        } else {
            return guestGetPopupStoreDto(popup);
        }
    } // 팝업 상세조회

    public String reopenDemand(Long userId, PushRequestDto pushRequestDto) {
        Long popupId = Long.valueOf(pushRequestDto.popupId());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        FCMToken token = fcmTokenRepository.findByToken(pushRequestDto.token());
        if (token == null) {
            throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);
        }

        ReopenDemand reopenDemand = new ReopenDemand(user, popup, pushRequestDto.token(), token.getMod_dtm(),
                token.getExp_dtm());
        reopenDemandRepository.save(reopenDemand);

        popup.addreopenDemandCnt(); // 재오픈 수요 + 1
        popupRepository.save(popup);

        /* 재오픈 체크 시 재오픈 토픽에 등록 */
        log.info("재오픈 수요 체크 시 FCM TOPIC 등록");
//        String pushToken = pushRequestDto.token();
//        fcmService.fcmAddTopic(pushToken, popup, EPopupTopic.REOPEN);

        return "재오픈 수요 체크 되었습니다.";
    }

    public List<PopupStoreDto> getPopupStoreDtos(Page<Popup> popups, Long userId) {
        // 방문자 데이터 리스트 및 실시간 방문자 수 리스트 생성
        List<VisitorDataInfoDto> visitorDataInfoDtos = new ArrayList<>();
        List<Optional<Integer>> visitorCntList = new ArrayList<>();
        List<Boolean> isBlockedList = new ArrayList<>();

        // 각 Popup에 대해 방문자 데이터 및 실시간 방문자 수를 조회하여 리스트에 추가
        for (Popup popup : popups.getContent()) {
            VisitorDataInfoDto visitorDataDto = visitorDataService.getVisitorData(popup.getId()); // 방문자 데이터
            visitorDataInfoDtos.add(visitorDataDto);

            Optional<Integer> visitorCnt = visitService.showRealTimeVisitors(popup.getId()); // 실시간 방문자 수
            visitorCntList.add(visitorCnt);

            Boolean idBlocked = blockedPopupRepository.existsByPopupIdAndUserId(popup.getId(), userId);
            isBlockedList.add(idBlocked);
        }

        // PopupStoreDto 리스트를 생성하여 반환
        return PopupStoreDto.fromEntities(popups.getContent(), visitorDataInfoDtos, visitorCntList, isBlockedList);
    }

    public List<PopupStoreDto> getPopupStoreDtos(List<Popup> popups, Long userId) {
        if (popups == null || popups.isEmpty()) {
            return null;
        }
        // 방문자 데이터 리스트 및 실시간 방문자 수 리스트 생성
        List<VisitorDataInfoDto> visitorDataInfoDtos = new ArrayList<>();
        List<Optional<Integer>> visitorCntList = new ArrayList<>();
        List<Boolean> isBlockedList = new ArrayList<>();

        // 각 Popup에 대해 방문자 데이터 및 실시간 방문자 수를 조회하여 리스트에 추가
        for (Popup popup : popups) {
            VisitorDataInfoDto visitorDataDto = visitorDataService.getVisitorData(popup.getId()); // 방문자 데이터
            visitorDataInfoDtos.add(visitorDataDto);

            Optional<Integer> visitorCnt = visitService.showRealTimeVisitors(popup.getId()); // 실시간 방문자 수
            visitorCntList.add(visitorCnt);

            Boolean idBlocked = blockedPopupRepository.existsByPopupIdAndUserId(popup.getId(), userId);
            isBlockedList.add(idBlocked);
        }

        // PopupStoreDto 리스트를 생성하여 반환
        return PopupStoreDto.fromEntities(popups, visitorDataInfoDtos, visitorCntList, isBlockedList);
    }

    public PopupStoreDto getPopupStoreDto(Popup popup, Long userId) {
        if (popup == null) {
            return null;
        }

        VisitorDataInfoDto visitorDataDto = visitorDataService.getVisitorData(popup.getId()); // 방문자 데이터

        Optional<Integer> visitorCnt = visitService.showRealTimeVisitors(popup.getId()); // 실시간 방문자 수

        Boolean idBlocked = blockedPopupRepository.existsByPopupIdAndUserId(popup.getId(), userId);

        // PopupStoreDto 리스트를 생성하여 반환
        return PopupStoreDto.fromEntity(popup, visitorDataDto, visitorCnt, idBlocked);
    }

    public List<PopupStoreDto> guestGetPopupStoreDtos(Page<Popup> popups) {
        // 방문자 데이터 리스트 및 실시간 방문자 수 리스트 생성
        List<VisitorDataInfoDto> visitorDataInfoDtos = new ArrayList<>();
        List<Optional<Integer>> visitorCntList = new ArrayList<>();

        // 각 Popup에 대해 방문자 데이터 및 실시간 방문자 수를 조회하여 리스트에 추가
        for (Popup popup : popups.getContent()) {
            VisitorDataInfoDto visitorDataDto = visitorDataService.getVisitorData(popup.getId()); // 방문자 데이터
            visitorDataInfoDtos.add(visitorDataDto);

            Optional<Integer> visitorCnt = visitService.showRealTimeVisitors(popup.getId()); // 실시간 방문자 수
            visitorCntList.add(visitorCnt);
        }

        // PopupStoreDto 리스트를 생성하여 반환
        return PopupStoreDto.fromEntities(popups.getContent(), visitorDataInfoDtos, visitorCntList);
    }

    public List<PopupStoreDto> guestGetPopupStoreDtos(List<Popup> popups) {
        if (popups == null || popups.isEmpty()) {
            return null;
        }
        // 방문자 데이터 리스트 및 실시간 방문자 수 리스트 생성
        List<VisitorDataInfoDto> visitorDataInfoDtos = new ArrayList<>();
        List<Optional<Integer>> visitorCntList = new ArrayList<>();

        // 각 Popup에 대해 방문자 데이터 및 실시간 방문자 수를 조회하여 리스트에 추가
        for (Popup popup : popups) {
            VisitorDataInfoDto visitorDataDto = visitorDataService.getVisitorData(popup.getId()); // 방문자 데이터
            visitorDataInfoDtos.add(visitorDataDto);

            Optional<Integer> visitorCnt = visitService.showRealTimeVisitors(popup.getId()); // 실시간 방문자 수
            visitorCntList.add(visitorCnt);
        }

        // PopupStoreDto 리스트를 생성하여 반환
        return PopupStoreDto.fromEntities(popups, visitorDataInfoDtos, visitorCntList);
    }

    public PopupStoreDto guestGetPopupStoreDto(Popup popup) {
        if (popup == null) {
            return null;
        }

        VisitorDataInfoDto visitorDataDto = visitorDataService.getVisitorData(popup.getId()); // 방문자 데이터

        Optional<Integer> visitorCnt = visitService.showRealTimeVisitors(popup.getId()); // 실시간 방문자 수

        // PopupStoreDto 리스트를 생성하여 반환
        return PopupStoreDto.fromEntity(popup, visitorDataDto, visitorCnt, false);
    }

}
