package com.poppin.poppinserver.interest.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.usecase.TokenQueryUseCase;
import com.poppin.poppinserver.alarm.usecase.TopicCommandUseCase;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.interest.domain.Interest;
import com.poppin.poppinserver.interest.dto.interest.request.InterestRequestDto;
import com.poppin.poppinserver.interest.dto.interest.response.InterestDto;
import com.poppin.poppinserver.interest.repository.InterestRepository;
import com.poppin.poppinserver.interest.usercase.InterestQueryUseCase;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.usecase.BlockedPopupQueryUseCase;
import com.poppin.poppinserver.popup.usecase.PopupQueryUseCase;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataInfoDto;
import com.poppin.poppinserver.visit.usecase.VisitQueryUseCase;
import com.poppin.poppinserver.visit.usecase.VisitorDataQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class InterestService {
    private final InterestRepository interestRepository;

    private final UserQueryUseCase userQueryUseCase;
    private final PopupQueryUseCase popupQueryUseCase;
    private final InterestQueryUseCase interestQueryUseCase;
    private final BlockedPopupQueryUseCase blockedPopupQueryUseCase;
    private final VisitQueryUseCase visitQueryUseCase;
    private final VisitorDataQueryUseCase visitorDataQueryUseCase;
    private final TokenQueryUseCase tokenQueryUseCase;
    private final TopicCommandUseCase topicCommandUseCase;

    @Transactional
    public InterestDto userAddInterest(Long userId, InterestRequestDto requestDto) throws FirebaseMessagingException {
        Long popupId = Long.valueOf(requestDto.popupId());

        interestRepository.findByUserIdAndPopupId(userId, popupId)
                .ifPresent(interest -> {
                    throw new CommonException(ErrorCode.DUPLICATED_INTEREST);
                });

        User user = userQueryUseCase.findUserById(userId);
        Popup popup = popupQueryUseCase.findPopupById(popupId);

        Interest interest = Interest.builder()
                .user(user)
                .popup(popup)
                .build();

        interestRepository.save(interest);

        popup.addInterestCnt();

        /*알림 구독*/
        String fcmToken = tokenQueryUseCase.findByUser(user).getToken();

        FCMToken token = tokenQueryUseCase.findByToken(fcmToken);
        topicCommandUseCase.subscribePopupTopic(token, popup, EPopupTopic.MAGAM);
        topicCommandUseCase.subscribePopupTopic(token, popup, EPopupTopic.OPEN);
        topicCommandUseCase.subscribePopupTopic(token, popup, EPopupTopic.CHANGE_INFO);

        VisitorDataInfoDto visitorDataDto = visitorDataQueryUseCase.findVisitorData(popup.getId()); // 방문자 데이터

        Optional<Integer> visitorCnt = visitQueryUseCase.getRealTimeVisitors(popup.getId()); // 실시간 방문자
        Boolean isBlocked = blockedPopupQueryUseCase.existBlockedPopupByUserIdAndPopupId(popup.getId(), userId);
        LocalDateTime interestCreatedAt = interestRepository.findCreatedAtByUserIdAndPopupId(userId, popup.getId());
        Boolean isVisited = visitQueryUseCase.findByUserId(userId, popup.getId()).isPresent();

        return InterestDto.fromEntity(interest, popup, visitorDataDto, visitorCnt, isBlocked, isVisited, interestCreatedAt);
    }

    @Transactional
    public InterestDto removeInterest(Long userId, InterestRequestDto requestDto) throws FirebaseMessagingException {
        Long popupId = Long.valueOf(requestDto.popupId());

        Interest interest = interestQueryUseCase.findInterestByUserIdAndPopupId(userId, popupId);
        User user = userQueryUseCase.findUserById(userId);
        Popup popup = popupQueryUseCase.findPopupById(popupId);

        VisitorDataInfoDto visitorDataDto = visitorDataQueryUseCase.findVisitorData(popup.getId()); // 방문자 데이터

        Optional<Integer> visitorCnt = visitQueryUseCase.getRealTimeVisitors(popup.getId()); // 실시간 방문자
        Boolean isBlocked = blockedPopupQueryUseCase.existBlockedPopupByUserIdAndPopupId(popup.getId(), userId);
        Boolean isVisited = visitQueryUseCase.findByUserId(userId, popup.getId()).isPresent();
        LocalDateTime interestCreatedAt = interestRepository.findCreatedAtByUserIdAndPopupId(userId, popup.getId());

        InterestDto interestDto = InterestDto.fromEntity(interest, popup, visitorDataDto, visitorCnt, isBlocked, isVisited, interestCreatedAt);

        interestRepository.delete(interest);

        String fcmToken = tokenQueryUseCase.findByUser(user).getToken();

        FCMToken token = tokenQueryUseCase.findByToken(fcmToken);
        topicCommandUseCase.unsubscribePopupTopic(token, popup, EPopupTopic.MAGAM);
        topicCommandUseCase.unsubscribePopupTopic(token, popup, EPopupTopic.OPEN);
        topicCommandUseCase.unsubscribePopupTopic(token, popup, EPopupTopic.CHANGE_INFO);

        return interestDto;
    }
}
