package com.poppin.poppinserver.popup.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.usecase.TokenQueryUseCase;
import com.poppin.poppinserver.alarm.usecase.TopicCommandUseCase;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.core.util.HeaderUtil;
import com.poppin.poppinserver.interest.repository.InterestRepository;
import com.poppin.poppinserver.interest.usercase.InterestQueryUseCase;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.popup.domain.Waiting;
import com.poppin.poppinserver.popup.dto.popup.response.*;
import com.poppin.poppinserver.popup.repository.BlockedPopupRepository;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.popup.repository.PosterImageRepository;
import com.poppin.poppinserver.popup.usecase.WaitingCommandUseCase;
import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.review.domain.ReviewImage;
import com.poppin.poppinserver.review.dto.response.ReviewInfoDto;
import com.poppin.poppinserver.review.repository.ReviewQueryRepository;
import com.poppin.poppinserver.review.usecase.ReviewImageQueryUseCase;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.BlockedUserQueryRepository;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import com.poppin.poppinserver.visit.domain.Visit;
import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataInfoDto;
import com.poppin.poppinserver.visit.usecase.VisitQueryUseCase;
import com.poppin.poppinserver.visit.usecase.VisitorDataQueryUseCase;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopupService {
    private final PopupRepository popupRepository;

    private final ReviewQueryRepository reviewRepository;
    private final PosterImageRepository posterImageRepository;
    private final BlockedUserQueryRepository blockedUserQueryRepository;
    private final BlockedPopupRepository blockedPopupRepository;
    private final HeaderUtil headerUtil;
    private final InterestRepository interestRepository;

    private final WaitingCommandUseCase waitingCommandUseCase;
    private final TopicCommandUseCase topicCommandUseCase;
    private final ReviewImageQueryUseCase reviewImageQueryUseCase;
    private final UserQueryUseCase userQueryUseCase;
    private final InterestQueryUseCase interestQueryUseCase;
    private final TokenQueryUseCase tokenQueryUseCase;
    private final VisitQueryUseCase visitQueryUseCase;
    private final VisitorDataQueryUseCase visitorDataQueryUseCase;

//    @Transactional
//    public String test() {
//        List<Popup> popups = popupRepository.findAllByOpStatusIsNotyetOrOperating();
//
//        for (Popup popup : popups) {
//            //현재 운영상태 수정
//            if (popup.getOpenDate().isAfter(LocalDate.now())) { // 오픈 전
//                log.info("getOpenDate: " + popup.getOpenDate().toString() +", getCloseDate : " + popup.getCloseDate().toString() + ", now: " + LocalDate.now().toString());
//                log.info("update: " + popup.getName() +", from : " + popup.getOperationStatus().toString());
//                popup.updateOpStatus(String.valueOf(EOperationStatus.NOTYET));
//                log.info("status: " + EOperationStatus.NOTYET.getStatus());
//                log.info("to: " + popup.getOperationStatus().toString());
//            } else if (popup.getCloseDate().isBefore(LocalDate.now())) { // 운영 종료
//                log.info("getOpenDate: " + popup.getOpenDate().toString() +", getCloseDate : " + popup.getCloseDate().toString() + ", now: " + LocalDate.now().toString());
//
//                log.info("update: " + popup.getName() +", from : " + popup.getOperationStatus().toString());
//                popup.updateOpStatus(EOperationStatus.TERMINATED.getStatus());
//                log.info("status: " + EOperationStatus.TERMINATED.getStatus());
//                log.info("to: " + popup.getOperationStatus().toString());
//
//                List<Visit> visits = visitRepository.findByPopupId(popup.getId());
//                for (Visit visit : visits) visitRepository.delete(visit);
//            } else { // 운영중
//                log.info("getOpenDate: " + popup.getOpenDate().toString() +", getCloseDate : " + popup.getCloseDate().toString() + ", now: " + LocalDate.now().toString());
//
//                log.info("update: " + popup.getName() +", from : " + popup.getOperationStatus().toString());
//                popup.updateOpStatus(EOperationStatus.OPERATING.getStatus());
//                log.info("status: " + EOperationStatus.OPERATING.getStatus());
//                log.info("to: " + popup.getOperationStatus().toString());
//
//            }
//        }
//
//        popupRepository.saveAll(popups);
//        return "asdf";
//    }

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
            List<ReviewImage> reviewImages = reviewImageQueryUseCase.findAllByReviewId(review.getId());

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

        VisitorDataInfoDto visitorDataDto = visitorDataQueryUseCase.findVisitorData(popupId); // 방문자 데이터

        Optional<Integer> visitors = visitQueryUseCase.getRealTimeVisitors(popupId); // 실시간 방문자

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

        List<Long> blockedUserIds = blockedUserQueryRepository.findBlockedUserIdsByUserId(userId);
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

            List<ReviewImage> reviewImages = reviewImageQueryUseCase.findAllByReviewId(review.getId());

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

        VisitorDataInfoDto visitorDataDto =  visitorDataQueryUseCase.findVisitorData(popupId); // 방문자 데이터

        Optional<Integer> visitors = visitQueryUseCase.getRealTimeVisitors(popupId); // 실시간 방문자

        popupRepository.save(popup);

        // 이미지 목록 가져오기
        List<PosterImage> posterImages = posterImageRepository.findAllByPopupId(popup);

        List<String> imageList = new ArrayList<>();
        for (PosterImage posterImage : posterImages) {
            imageList.add(posterImage.getPosterUrl());
        }

        // 관심 여부 확인
        Boolean isInterested = interestQueryUseCase.existsInterestByUserIdAndPopupId(userId, popupId);

        Optional<Visit> visit = visitQueryUseCase.findByUserId(userId, popupId);

        // 차단 여부 확인
        User user = userQueryUseCase.findUserById(userId);
        Boolean isBlocked = blockedPopupRepository.findByPopupIdAndUserId(popup, user).isPresent();

        // 방문 여부 확인
        if (!visit.equals(null)) {
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

        popup.addViewCnt(); // 조회수 + 1
        popupRepository.save(popup);

        if (userId != null) {
            return getPopupStoreDto(popup, userId);
        } else {
            return guestGetPopupStoreDto(popup);
        }
    } // 팝업 상세조회

    // 재오픈 신청
    public PopupReopenDto waiting(Long userId, String SpopupId) throws FirebaseMessagingException {
        Long popupId = Long.valueOf(SpopupId);

        User user = userQueryUseCase.findUserById(userId);

        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));


        if (!popup.getOperationStatus().equals("TERMINATED")){ // 운영 종료 상태인지 확인
            throw new CommonException(ErrorCode.SERVER_ERROR);
        }

        FCMToken token = tokenQueryUseCase.findTokenByUserId(user.getId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TOKEN));


        // Waiting 저장
        Waiting waiting = waitingCommandUseCase.save(new Waiting(user, popup));

        // Popup 업데이트 및 저장
        popup.addreopenDemandCnt();
        popupRepository.save(popup);

        // 재오픈 토픽 등록
        log.info("재오픈 신청 시 FCM TOPIC 등록");
        topicCommandUseCase.subscribePopupTopic(user, token, popup, EPopupTopic.REOPEN);


        PopupStoreDto popupStoreDto = getPopupStoreDto(popup,user.getId());
        PopupWaitingDto popupWaitingDto = PopupWaitingDto.fromEntity(waiting.getId(), popupId);

        // DTO 반환
        return PopupReopenDto.fromEntity(popupStoreDto, popupWaitingDto);

    }


    public List<PopupStoreDto> getPopupStoreDtos(Page<Popup> popups, Long userId) {
        // 방문자 데이터 리스트 및 실시간 방문자 수 리스트 생성
        List<VisitorDataInfoDto> visitorDataInfoDtos = new ArrayList<>();
        List<Optional<Integer>> visitorCntList = new ArrayList<>();
        List<Boolean> isBlockedList = new ArrayList<>();

        // 각 Popup에 대해 방문자 데이터 및 실시간 방문자 수를 조회하여 리스트에 추가
        for (Popup popup : popups.getContent()) {
            VisitorDataInfoDto visitorDataDto = visitorDataQueryUseCase.findVisitorData(popup.getId()); // 방문자 데이터
            visitorDataInfoDtos.add(visitorDataDto);

            Optional<Integer> visitorCnt = visitQueryUseCase.getRealTimeVisitors(popup.getId()); // 실시간 방문자 수
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
        List<LocalDateTime> interestCreatedAtList = new ArrayList<>();

        // 각 Popup에 대해 방문자 데이터 및 실시간 방문자 수를 조회하여 리스트에 추가
        for (Popup popup : popups) {
            VisitorDataInfoDto visitorDataDto =  visitorDataQueryUseCase.findVisitorData(popup.getId()); // 방문자 데이터
            visitorDataInfoDtos.add(visitorDataDto);

            Optional<Integer> visitorCnt = visitQueryUseCase.getRealTimeVisitors(popup.getId()); // 실시간 방문자 수
            visitorCntList.add(visitorCnt);

            Boolean idBlocked = blockedPopupRepository.existsByPopupIdAndUserId(popup.getId(), userId);
            isBlockedList.add(idBlocked);

            LocalDateTime interestCreatedAt = interestRepository.findCreatedAtByUserIdAndPopupId(userId, popup.getId());
            interestCreatedAtList.add(interestCreatedAt);
        }

        // PopupStoreDto 리스트를 생성하여 반환
        return PopupStoreDto.fromEntities(popups, visitorDataInfoDtos, visitorCntList, isBlockedList, interestCreatedAtList);
    }

    public PopupStoreDto getPopupStoreDto(Popup popup, Long userId) {
        if (popup == null) {
            return null;
        }

        VisitorDataInfoDto visitorDataDto =  visitorDataQueryUseCase.findVisitorData(popup.getId()); // 방문자 데이터

        Optional<Integer> visitorCnt = visitQueryUseCase.getRealTimeVisitors(popup.getId()); // 실시간 방문자 수

        Boolean idBlocked = blockedPopupRepository.existsByPopupIdAndUserId(popup.getId(), userId);

        LocalDateTime interestCreatedAt = interestRepository.findCreatedAtByUserIdAndPopupId(userId, popup.getId());

        // PopupStoreDto 리스트를 생성하여 반환
        return PopupStoreDto.fromEntity(popup, visitorDataDto, visitorCnt, idBlocked, interestCreatedAt);
    }

    public List<PopupStoreDto> guestGetPopupStoreDtos(Page<Popup> popups) {
        // 방문자 데이터 리스트 및 실시간 방문자 수 리스트 생성
        List<VisitorDataInfoDto> visitorDataInfoDtos = new ArrayList<>();
        List<Optional<Integer>> visitorCntList = new ArrayList<>();

        // 각 Popup에 대해 방문자 데이터 및 실시간 방문자 수를 조회하여 리스트에 추가
        for (Popup popup : popups.getContent()) {
            VisitorDataInfoDto visitorDataDto =  visitorDataQueryUseCase.findVisitorData(popup.getId()); // 방문자 데이터
            visitorDataInfoDtos.add(visitorDataDto);

            Optional<Integer> visitorCnt = visitQueryUseCase.getRealTimeVisitors(popup.getId()); // 실시간 방문자 수
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
            VisitorDataInfoDto visitorDataDto = visitorDataQueryUseCase.findVisitorData(popup.getId()); // 방문자 데이터
            visitorDataInfoDtos.add(visitorDataDto);

            Optional<Integer> visitorCnt = visitQueryUseCase.getRealTimeVisitors(popup.getId()); // 실시간 방문자 수
            visitorCntList.add(visitorCnt);
        }

        // PopupStoreDto 리스트를 생성하여 반환
        return PopupStoreDto.fromEntities(popups, visitorDataInfoDtos, visitorCntList);
    }

    public PopupStoreDto guestGetPopupStoreDto(Popup popup) {
        if (popup == null) {
            return null;
        }

        VisitorDataInfoDto visitorDataDto =  visitorDataQueryUseCase.findVisitorData(popup.getId()); // 방문자 데이터

        Optional<Integer> visitorCnt = visitQueryUseCase.getRealTimeVisitors(popup.getId()); // 실시간 방문자 수

        // PopupStoreDto 리스트를 생성하여 반환
        return PopupStoreDto.fromEntity(popup, visitorDataDto, visitorCnt, false, null);
    }

    public List<VisitedPopupDto> getVisitedPopupList(Long userId) {

        List<Visit> visitList = visitQueryUseCase.findAllByUserId(userId);
        if (visitList.isEmpty()) {
            throw new CommonException(ErrorCode.NOT_FOUND_VISIT);
        }

        List<Long> visitedPopupIds = visitList.stream()
                .map(visit -> visit.getPopup().getId())
                .collect(Collectors.toList());

        log.info("visited popup ids: {}" , visitedPopupIds);
        List<Popup> unreviewedPopups = popupRepository.findUnreviewedPopups(visitedPopupIds, userId);


        if (unreviewedPopups.isEmpty()) {
            return null; // 데이터가 없을 시 빈 값을 반환
        }

        return unreviewedPopups.stream()
                .map(VisitedPopupDto::fromEntity)
                .collect(Collectors.toList());
    }
}
