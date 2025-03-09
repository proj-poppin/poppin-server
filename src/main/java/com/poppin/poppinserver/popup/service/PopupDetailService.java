package com.poppin.poppinserver.popup.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.usecase.TokenQueryUseCase;
import com.poppin.poppinserver.alarm.usecase.TopicCommandUseCase;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.core.util.HeaderUtil;
import com.poppin.poppinserver.interest.usercase.InterestQueryUseCase;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.Waiting;
import com.poppin.poppinserver.popup.dto.popup.response.*;
import com.poppin.poppinserver.popup.repository.PopupCommandRepository;
import com.poppin.poppinserver.popup.repository.PopupQueryRepository;
import com.poppin.poppinserver.popup.usecase.BlockedPopupQueryUseCase;
import com.poppin.poppinserver.popup.usecase.WaitingCommandUseCase;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import com.poppin.poppinserver.visit.domain.Visit;
import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataInfoDto;
import com.poppin.poppinserver.visit.usecase.VisitQueryUseCase;
import com.poppin.poppinserver.visit.usecase.VisitorDataQueryUseCase;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopupDetailService {
    private final PopupQueryRepository popupQueryRepository;
    private final PopupCommandRepository popupCommandRepository;

    private final WaitingCommandUseCase waitingCommandUseCase;
    private final TopicCommandUseCase topicCommandUseCase;
    private final UserQueryUseCase userQueryUseCase;
    private final TokenQueryUseCase tokenQueryUseCase;
    private final VisitQueryUseCase visitQueryUseCase;
    private final VisitorDataQueryUseCase visitorDataQueryUseCase;
    private final BlockedPopupQueryUseCase blockedPopupQueryUseCase;
    private final InterestQueryUseCase interestQueryUseCase;

    private final HeaderUtil headerUtil;

    public PopupStoreDto readPopupStore(String strPopupId, HttpServletRequest request) {
        Long popupId = Long.valueOf(strPopupId);

        Long userId = headerUtil.parseUserId(request);
        Popup popup = popupQueryRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        popup.addViewCnt(); // 조회수 + 1
        popupQueryRepository.save(popup);

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

        Popup popup = popupQueryRepository.findById(popupId)
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
        popupCommandRepository.save(popup);

        // 재오픈 토픽 등록
        log.info("재오픈 신청 시 FCM TOPIC 등록");
        topicCommandUseCase.subscribePopupTopic(user, token, popup, EPopupTopic.REOPEN);


        PopupStoreDto popupStoreDto = getPopupStoreDto(popup,user.getId());
        PopupWaitingDto popupWaitingDto = PopupWaitingDto.fromEntity(waiting.getId(), popupId);

        // DTO 반환
        return PopupReopenDto.fromEntity(popupStoreDto, popupWaitingDto);

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
        List<Popup> unreviewedPopups = popupQueryRepository.findUnreviewedPopups(visitedPopupIds, userId);


        if (unreviewedPopups.isEmpty()) {
            return null; // 데이터가 없을 시 빈 값을 반환
        }

        return unreviewedPopups.stream()
                .map(VisitedPopupDto::fromEntity)
                .collect(Collectors.toList());
    }

    //  PopupStoreDto 반환 메서드
    public List<PopupStoreDto> getPopupStoreDtos(List<Popup> popups, Long userId) {
        List<PopupStoreDto> popupStoreDtos = new ArrayList<>();
        if (popups == null || popups.isEmpty()) {
            return popupStoreDtos;
        }

        for (Popup popup : popups) {
            popupStoreDtos.add(
                    getPopupStoreDto(popup, userId)
            );
        }

        // PopupStoreDto 리스트를 생성하여 반환
        return popupStoreDtos;
    }

    public PopupStoreDto getPopupStoreDto(Popup popup, Long userId) {
        if (popup == null) {
            return null;
        }

        VisitorDataInfoDto visitorDataDto =  visitorDataQueryUseCase.findVisitorData(popup.getId()); // 방문자 데이터

        Optional<Integer> visitorCnt = visitQueryUseCase.getRealTimeVisitors(popup.getId()); // 실시간 방문자 수

        Boolean idBlocked = blockedPopupQueryUseCase.existBlockedPopupByUserIdAndPopupId(userId, popup.getId());

        LocalDateTime interestCreatedAt = interestQueryUseCase.findCreatedAtByUserIdAndPopupId(userId, popup.getId());

        // PopupStoreDto 리스트를 생성하여 반환
        return PopupStoreDto.fromEntity(popup, visitorDataDto, visitorCnt, idBlocked, interestCreatedAt);
    }

    public List<PopupStoreDto> guestGetPopupStoreDtos(List<Popup> popups) {
        List<PopupStoreDto> popupStoreDtos = new ArrayList<>();
        if (popups == null || popups.isEmpty()) {
            return popupStoreDtos;
        }

        // 각 Popup에 대해 방문자 데이터 및 실시간 방문자 수를 조회하여 리스트에 추가
        for (Popup popup : popups) {
            popupStoreDtos.add(
                    guestGetPopupStoreDto(popup)
            );
        }

        // PopupStoreDto 리스트를 생성하여 반환
        return popupStoreDtos;
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
}
