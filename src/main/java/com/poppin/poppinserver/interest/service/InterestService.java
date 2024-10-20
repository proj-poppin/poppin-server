package com.poppin.poppinserver.interest.service;

import com.poppin.poppinserver.alarm.service.FCMTokenService;
import com.poppin.poppinserver.interest.dto.interest.request.InterestRequestDto;
import com.poppin.poppinserver.interest.dto.interest.response.InterestDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.interest.domain.Interest;
import com.poppin.poppinserver.interest.repository.InterestRepository;
import com.poppin.poppinserver.interest.usercase.InterestQueryUseCase;
import com.poppin.poppinserver.popup.domain.BlockedPopup;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.repository.BlockedPopupRepository;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.popup.usecase.BlockedPopupQueryUseCase;
import com.poppin.poppinserver.popup.usecase.PopupQueryUseCase;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.UserRepository;
import com.poppin.poppinserver.user.usecase.ReadUserUseCase;
import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataInfoDto;
import com.poppin.poppinserver.visit.service.VisitService;
import com.poppin.poppinserver.visit.service.VisitorDataService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class InterestService {
    private final InterestRepository interestRepository;

    private final FCMTokenService fcmTokenService;
    private final VisitorDataService visitorDataService;
    private final VisitService visitService;

    private final ReadUserUseCase readUserUseCase;
    private final PopupQueryUseCase popupQueryUseCase;
    private final InterestQueryUseCase interestQueryUseCase;
    private final BlockedPopupQueryUseCase blockedPopupQueryUseCase;

    @Transactional
    public InterestDto userAddInterest(Long userId, InterestRequestDto requestDto) {
        Long popupId = Long.valueOf(requestDto.popupId());

        interestRepository.findByUserIdAndPopupId(userId, popupId)
                .ifPresent(interest -> {
                    throw new CommonException(ErrorCode.DUPLICATED_INTEREST);
                });

        User user = readUserUseCase.findUserById(userId);
        Popup popup = popupQueryUseCase.findPopupById(popupId);

        Interest interest = Interest.builder()
                .user(user)
                .popup(popup)
                .build();

        interestRepository.save(interest);

        popup.addInterestCnt();

        /*알림 구독*/
        String fcmToken = requestDto.fcmToken();
        fcmTokenService.fcmAddPopupTopic(fcmToken, popup, EPopupTopic.MAGAM);
        fcmTokenService.fcmAddPopupTopic(fcmToken, popup, EPopupTopic.OPEN);
        fcmTokenService.fcmAddPopupTopic(fcmToken, popup, EPopupTopic.CHANGE_INFO);

        VisitorDataInfoDto visitorDataDto = visitorDataService.getVisitorData(popup.getId()); // 방문자 데이터
        Optional<Integer> visitorCnt = visitService.showRealTimeVisitors(popup.getId()); // 실시간 방문자
        Boolean isBlocked = blockedPopupQueryUseCase.existBlockedPopupByUserIdAndPopupId(popup.getId(), userId);

        return InterestDto.fromEntity(interest, popup, visitorDataDto, visitorCnt, isBlocked);
    }

    public InterestDto removeInterest(Long userId, InterestRequestDto requestDto) {
        Long popupId = Long.valueOf(requestDto.popupId());

        Interest interest = interestQueryUseCase.findInterestByUserIdAndPopupId(userId, popupId);

        Popup popup = popupQueryUseCase.findPopupById(popupId);

        VisitorDataInfoDto visitorDataDto = visitorDataService.getVisitorData(popup.getId()); // 방문자 데이터
        Optional<Integer> visitorCnt = visitService.showRealTimeVisitors(popup.getId()); // 실시간 방문자
        Boolean isBlocked = blockedPopupQueryUseCase.existBlockedPopupByUserIdAndPopupId(popup.getId(), userId);

        InterestDto interestDto = InterestDto.fromEntity(interest, popup, visitorDataDto, visitorCnt, isBlocked);

        interestRepository.delete(interest);

        /*FCM 구독취소*/
        String fcmToken = requestDto.fcmToken();

        fcmTokenService.fcmRemovePopupTopic(fcmToken, popup, EPopupTopic.MAGAM);
        fcmTokenService.fcmRemovePopupTopic(fcmToken, popup, EPopupTopic.OPEN);
        fcmTokenService.fcmRemovePopupTopic(fcmToken, popup, EPopupTopic.CHANGE_INFO);

        return interestDto;
    }
}
