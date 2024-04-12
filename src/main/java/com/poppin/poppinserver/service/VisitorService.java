package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.Visitor;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.dto.popup.request.PopupInfoDto;
import com.poppin.poppinserver.dto.realtimeVisit.response.RealTimeVisitorsDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.PopupRepository;
import com.poppin.poppinserver.repository.VisitorRepository;
import com.poppin.poppinserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class VisitorService {

    private final VisitorRepository visitorRepository;
    private final UserRepository userRepository;
    private final PopupRepository popupRepository;

    /* 실시간 방문자 조회 */
    public Optional<Integer> showRealTimeVisitors(Long popupId){

        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime thirtyMinutesAgo = now.minus(30, ChronoUnit.MINUTES);

        Optional<Integer> realTimeVisitorsCount = visitorRepository.showRealTimeVisitors(popup, thirtyMinutesAgo);

        return realTimeVisitorsCount;
    }

    /*방문하기 버튼 누를 시*/
    public RealTimeVisitorsDto addRealTimeVisitors(Long userId, PopupInfoDto popupInfoDto){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(popupInfoDto.popupId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        /*30분 전 시간*/
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minus(30, ChronoUnit.MINUTES);

        Visitor realTimeVisit = Visitor.builder()
                .user(user)
                .popup(popup)
                .build();

        int visitors = visitorRepository.findDuplicateVisitors(user,popup, thirtyMinutesAgo);
        if (visitors > 0)throw new CommonException(ErrorCode.DUPLICATED_REALTIME_VISIT); // 30분 이내 재 방문 방지

        visitorRepository.save(realTimeVisit); /*마이페이지 - 후기 작성하기 시 보여야하기에 배치돌며 이주일 전 생성된 데이터만 삭제 예정*/

        Optional<Integer> realTimeVisitorsCount = visitorRepository.showRealTimeVisitors(popup, thirtyMinutesAgo); /*실시간 방문자 수*/

        if (realTimeVisitorsCount.isEmpty()){realTimeVisitorsCount = Optional.of(0);} // empty 면 0으로.

            RealTimeVisitorsDto realTimeVisitorsDto = RealTimeVisitorsDto.builder()
                .userId(realTimeVisit.getUser().getId())
                .popupId(realTimeVisit.getPopup().getId())
                .visitorsCnt(realTimeVisitorsCount)
                .build();

        return realTimeVisitorsDto;
    }
}
