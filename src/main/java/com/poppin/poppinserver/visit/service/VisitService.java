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
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.UserRepository;
import com.poppin.poppinserver.visit.domain.Visit;
import com.poppin.poppinserver.visit.dto.visit.response.VisitResponseDto;
import com.poppin.poppinserver.visit.dto.visit.response.VisitStatusDto;
import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataInfoDto;
import com.poppin.poppinserver.visit.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public VisitResponseDto visit(Long userId, VisitorsInfoDto visitorsInfoDto) {

        // 사용자 및 팝업 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(visitorsInfoDto.popupId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        // 30분 전 시간 계산
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minus(30, ChronoUnit.MINUTES);

        // 중복 방문 검사 (30분 이내 재방문 방지)
        if (visitRepository.findDuplicateVisitors(userId, popup.getId(), thirtyMinutesAgo) > 0) {
            throw new CommonException(ErrorCode.DUPLICATED_REALTIME_VISIT);
        }

        // 방문 이력 조회
        Optional<Visit> existingVisit = visitRepository.findByUserId(userId, visitorsInfoDto.popupId());

        if (existingVisit.isPresent()) {
            // 재오픈 시 상태 변경
            existingVisit.get().changeStatus("VISIT_NOW");
            visitRepository.save(existingVisit.get());
        } else {
            // 새로운 방문 처리
            Visit newVisit = Visit.builder()
                    .user(user)
                    .popup(popup)
                    .status("VISIT_COMPLETE")
                    .build();
            visitRepository.save(newVisit);
            user.addVisitedPopupCnt(); // 사용자의 방문 팝업 수 증가

            // FCM 토큰 조회 및 구독 처리
            FCMToken fcmToken = fcmTokenRepository.findByUserId(userId)
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TOKEN));
            fcmTokenService.fcmAddPopupTopic(fcmToken.getToken(), popup, EPopupTopic.HOOGI);
        }

        // 실시간 방문자 수 조회 (Optional의 처리 간소화)
        int realTimeVisitors = visitRepository.showRealTimeVisitors(popup, thirtyMinutesAgo)
                .orElse(0); // 값이 없으면 0으로 처리

        // 방문자 데이터 및 응답 객체 생성
        VisitorDataInfoDto visitorDataDto = visitorDataService.getVisitorData(popup.getId());
        PopupStoreDto popupStoreDto = PopupStoreDto.fromEntity(popup, visitorDataDto, Optional.of(realTimeVisitors));
        VisitStatusDto visitStatusDto = VisitStatusDto.fromEntity(
                existingVisit.map(Visit::getId).orElse(null),
                popup.getId(),
                user.getId(),
                existingVisit.map(Visit::getStatus).orElse("VISIT_COMPLETE"),
                LocalDate.now()
        );

        return VisitResponseDto.fromEntity(popupStoreDto, visitStatusDto);
    }


    public void changeVisitStatus(Long popupId){
        List<Visit> visitList = visitRepository.findByPopupId(popupId);
        for (Visit v: visitList){
            v.changeStatus("RECEIVE_REOPEN_ALERT");
            visitRepository.save(v);
        }
    }
}
