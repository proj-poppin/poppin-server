package com.poppin.poppinserver.visit.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.usecase.TokenQueryUseCase;
import com.poppin.poppinserver.alarm.usecase.TopicCommandUseCase;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.interest.usercase.InterestQueryUseCase;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.dto.popup.request.VisitorsInfoDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupStoreDto;
import com.poppin.poppinserver.popup.usecase.BlockedPopupQueryUseCase;
import com.poppin.poppinserver.popup.usecase.PopupQueryUseCase;
import com.poppin.poppinserver.review.repository.ReviewQueryRepository;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import com.poppin.poppinserver.visit.domain.Visit;
import com.poppin.poppinserver.visit.dto.visit.response.VisitDto;
import com.poppin.poppinserver.visit.dto.visit.response.VisitedPopupDto;
import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataInfoDto;
import com.poppin.poppinserver.visit.repository.VisitRepository;
import com.poppin.poppinserver.visit.usecase.VisitorDataQueryUseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;


    private final UserQueryUseCase userQueryUseCase;
    private final BlockedPopupQueryUseCase blockedPopupQueryUseCase;
    private final PopupQueryUseCase popupQueryUseCase;
    private final VisitorDataQueryUseCase visitorDataQueryUseCase;
    private final TokenQueryUseCase tokenQueryUseCase;
    private final TopicCommandUseCase topicCommandUseCase;
    private final InterestQueryUseCase interestQueryUseCase;
    private final ReviewQueryRepository reviewQueryRepository;

    /* 실시간 방문자 조회 */
    public Optional<Integer> showRealTimeVisitors(Long popupId) {

        Popup popup = popupQueryUseCase.findPopupById(popupId);

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime thirtyMinutesAgo = now.minus(30, ChronoUnit.MINUTES);

        int realTimeVisitorsCount = visitRepository.showRealTimeVisitors(popup, thirtyMinutesAgo)
                .orElse(0);

        return Optional.of(realTimeVisitorsCount);
    }

    @Transactional
    /*방문하기 버튼 누를 시*/
    public VisitedPopupDto visit(Long userId, VisitorsInfoDto visitorsInfoDto) throws FirebaseMessagingException {
        Long popupId = Long.valueOf(visitorsInfoDto.popupId());

        User user = userQueryUseCase.findUserById(userId);
        Popup popup = popupQueryUseCase.findPopupById(popupId);

        // 이미 일반후기를 작성했을 시 에러 처리
        reviewQueryRepository.findByUserIdAndPopupId(userId, popupId)
                .ifPresent(review -> { throw new CommonException(ErrorCode.ALREADY_WRITTEN_REVIEW); });


        // 30분 이내 재 방문 방지
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minus(30, ChronoUnit.MINUTES);
        Integer duplicateVisitors = visitRepository.findDuplicateVisitors(userId, popup.getId(), thirtyMinutesAgo);
        if (duplicateVisitors > 0) {
            throw new CommonException(ErrorCode.DUPLICATED_REALTIME_VISIT);
        }

        Optional<FCMToken> token = tokenQueryUseCase.findTokenByUserId(userId);
        if (token.isEmpty()) {
            throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);
        } else {
            topicCommandUseCase.subscribePopupTopic(user, token.get(), popup, EPopupTopic.HOOGI);
        }


        Visit visit = visitRepository.save(
                Visit.builder()
                        .user(user)
                        .popup(popup)
                        .build()
        );

        // 사용자 리뷰 카운트 증가
        user.addVisitedPopupCnt();

        VisitorDataInfoDto visitorDataDto = visitorDataQueryUseCase.findVisitorData(popup.getId());
        Optional<Integer> visitorCnt = showRealTimeVisitors(popup.getId()); // 실시간 방문자
        Boolean isBlocked = blockedPopupQueryUseCase.existBlockedPopupByUserIdAndPopupId(userId, popup.getId());
        LocalDateTime interestCreatedAt = interestQueryUseCase.findCreatedAtByUserIdAndPopupId(userId, popup.getId());

        PopupStoreDto popupStoreDto = PopupStoreDto.fromEntity(popup, visitorDataDto, visitorCnt, isBlocked, interestCreatedAt);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = visit.getCreatedAt().format(formatter);
        VisitDto visitDto = VisitDto.fromEntity(visit.getId(), popup.getId(), user.getId(), date);

        return VisitedPopupDto.fromEntity(popupStoreDto, visitDto);
    }

}
