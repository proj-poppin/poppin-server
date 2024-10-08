package com.poppin.poppinserver.visit.service;

import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.repository.FCMTokenRepository;
import com.poppin.poppinserver.alarm.service.FCMTokenService;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.dto.popup.request.VisitorsInfoDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupStoreDto;
import com.poppin.poppinserver.popup.repository.BlockedPopupRepository;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.UserRepository;
import com.poppin.poppinserver.visit.domain.Visit;
import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataInfoDto;
import com.poppin.poppinserver.visit.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;
    private final UserRepository userRepository;
    private final PopupRepository popupRepository;
    private final FCMTokenRepository fcmTokenRepository;
    private final FCMTokenService fcmTokenService;
    private final VisitorDataService visitorDataService;
    private final BlockedPopupRepository blockedPopupRepository;


    /* 실시간 방문자 조회 */
    public Optional<Integer> showRealTimeVisitors(Long popupId) {

        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime thirtyMinutesAgo = now.minus(30, ChronoUnit.MINUTES);

        Optional<Integer> realTimeVisitorsCount = visitRepository.showRealTimeVisitors(popup, thirtyMinutesAgo);

        return realTimeVisitorsCount;
    }

    /*방문하기 버튼 누를 시*/
    public PopupStoreDto visit(Long userId, VisitorsInfoDto visitorsInfoDto) {
        Long popupId = Long.valueOf(visitorsInfoDto.popupId());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        /*30분 전 시간*/
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minus(30, ChronoUnit.MINUTES);

        Integer duplicateVisitors = visitRepository.findDuplicateVisitors(userId, popup.getId(), thirtyMinutesAgo);
        if (duplicateVisitors > 0) {
            throw new CommonException(ErrorCode.DUPLICATED_REALTIME_VISIT); // 30분 이내 재 방문 방지
        }

        Optional<Visit> visit = visitRepository.findByUserId(userId, popupId);

        //재오픈인경우
        if (visit.isPresent()){
            visit.get().changeStatus("VISIT_NOW");
            visitRepository.save(visit.get());
        }else{
            //방문하기
            Visit visitor = Visit.builder()
                    .user(user)
                    .popup(popup)
                    .status("VISIT_COMPLETE")
                    .build();

            visitRepository.save(visitor);
            user.addVisitedPopupCnt(); // 방문한 팝업 수 증가

            // fcm 구독
            Optional<FCMToken> token = fcmTokenRepository.findByUserId(userId);
            if (token.isEmpty()) {
                throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);
            }else{
                String fcmToken = token.get().getToken();
                fcmTokenService.fcmAddPopupTopic(fcmToken, popup, EPopupTopic.HOOGI);
            }

            Optional<Integer> realTimeVisitorsCount = visitRepository.showRealTimeVisitors(popup,
                    thirtyMinutesAgo); /*실시간 방문자 수*/

            if (realTimeVisitorsCount.isEmpty()) {
                realTimeVisitorsCount = Optional.of(0);
            } // empty 면 0으로.
        }

        VisitorDataInfoDto visitorDataDto = visitorDataService.getVisitorData(popup.getId()); // 방문자 데이터
        Optional<Integer> visitorCnt = showRealTimeVisitors(popup.getId()); // 실시간 방문자
        Boolean isBlocked = blockedPopupRepository.existsByPopupIdAndUserId(popup.getId(), userId);

        PopupStoreDto popupStoreDto = PopupStoreDto.fromEntity(popup,visitorDataDto,visitorCnt, isBlocked);
        return popupStoreDto;
    }

    public void changeVisitStatus(Long popupId){
        List<Visit> visitList = visitRepository.findByPopupId(popupId);
        for (Visit v: visitList){
            v.changeStatus("RECEIVE_REOPEN_ALERT");
            visitRepository.save(v);
        }
    }
}
