package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.Visit;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.dto.popup.request.VisitorsInfoDto;
import com.poppin.poppinserver.dto.visit.response.RealTimeVisitorsDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.PopupRepository;
import com.poppin.poppinserver.repository.VisitRepository;
import com.poppin.poppinserver.repository.UserRepository;
import com.poppin.poppinserver.type.EPopupTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;
    private final UserRepository userRepository;
    private final PopupRepository popupRepository;

    private final FCMTokenService fcmTokenService;

    /* 실시간 방문자 조회 */
    public Optional<Integer> showRealTimeVisitors(Long popupId){

        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime thirtyMinutesAgo = now.minus(30, ChronoUnit.MINUTES);

        Optional<Integer> realTimeVisitorsCount = visitRepository.showRealTimeVisitors(popup, thirtyMinutesAgo);

        return realTimeVisitorsCount;
    }

    /*방문하기 버튼 누를 시*/
    public RealTimeVisitorsDto addRealTimeVisitors(Long userId, VisitorsInfoDto visitorsInfoDto){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(visitorsInfoDto.popupId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        /*30분 전 시간*/
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minus(30, ChronoUnit.MINUTES);

        Visit realTimeVisit = Visit.builder()
                .user(user)
                .popup(popup)
                .build();

        Integer visitors = visitRepository.findDuplicateVisitors(userId,popup.getId(), thirtyMinutesAgo);
        if (visitors > 0)throw new CommonException(ErrorCode.DUPLICATED_REALTIME_VISIT); // 30분 이내 재 방문 방지

        visitRepository.save(realTimeVisit); /*마이페이지 - 후기 요청하기 시 보여야하기에 배치돌며 일 주일 전 생성된 데이터만 삭제 예정*/

        // fcm 구독
        String token = visitorsInfoDto.fcmToken();
        if (token.isEmpty()) throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);
        fcmTokenService.fcmAddTopic(token, popup, EPopupTopic.HOOGI);

        Optional<Integer> realTimeVisitorsCount = visitRepository.showRealTimeVisitors(popup, thirtyMinutesAgo); /*실시간 방문자 수*/

        if (realTimeVisitorsCount.isEmpty()){realTimeVisitorsCount = Optional.of(0);} // empty 면 0으로.

            RealTimeVisitorsDto realTimeVisitorsDto = RealTimeVisitorsDto.builder()
                .userId(realTimeVisit.getUser().getId())
                .popupId(realTimeVisit.getPopup().getId())
                .visitorsCnt(realTimeVisitorsCount)
                .build();

        return realTimeVisitorsDto;
    }
}
