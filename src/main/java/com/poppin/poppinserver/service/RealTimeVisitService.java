package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.RealTimeVisit;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.dto.RealTimeVisit.request.AddVisitorsDto;
import com.poppin.poppinserver.dto.RealTimeVisit.response.RealTimeVisitorsDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.PopupRepository;
import com.poppin.poppinserver.repository.RealTimeVisitRepository;
import com.poppin.poppinserver.repository.ReviewRepository;
import com.poppin.poppinserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RealTimeVisitService {

    private final RealTimeVisitRepository realTimeVisitRepository;
    private final UserRepository userRepository;
    private final PopupRepository popupRepository;
    private final ReviewRepository reviewRepository;

    public Optional<Integer> showRealTimeVisitors(Long popupId){

        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime thirtyMinutesAgo = now.minus(30, ChronoUnit.MINUTES);

        Optional<Integer> realTimeVisitorsCount = realTimeVisitRepository.showRealTimeVisitors(popup, thirtyMinutesAgo);

        return realTimeVisitorsCount;
    }

    public RealTimeVisitorsDto addRealTimeVisitors(Long userId, AddVisitorsDto addVisitorsDto){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(addVisitorsDto.popupId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime thirtyMinutesAgo = now.minus(30, ChronoUnit.MINUTES);

        RealTimeVisit realTimeVisit = RealTimeVisit.builder()
                .user(user)
                .popup(popup)
                .build();

        int visitors = realTimeVisitRepository.findDuplicateVisitors(user,popup, thirtyMinutesAgo);
        if (visitors > 0)throw new CommonException(ErrorCode.DUPLICATED_REALTIMEVISIT); // 30분 이내 재 방문 방지

        realTimeVisitRepository.save(realTimeVisit);

        Optional<Integer> realTimeVisitorsCount = realTimeVisitRepository.showRealTimeVisitors(popup, thirtyMinutesAgo);

        RealTimeVisitorsDto realTimeVisitorsDto = RealTimeVisitorsDto.builder()
                .userId(realTimeVisit.getUser().getId())
                .popupId(realTimeVisit.getPopup().getId())
                .visitorsCnt(realTimeVisitorsCount)
                .build();

        return realTimeVisitorsDto;
    }
}
