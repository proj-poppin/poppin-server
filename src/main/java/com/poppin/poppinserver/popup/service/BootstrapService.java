package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.alarm.domain.InformAlarm;
import com.poppin.poppinserver.alarm.dto.informAlarm.response.NoticeDto;
import com.poppin.poppinserver.alarm.usecase.AlarmQueryUseCase;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.util.HeaderUtil;
import com.poppin.poppinserver.interest.domain.Interest;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.dto.popup.response.BootstrapDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupStoreDto;
import com.poppin.poppinserver.popup.usecase.PopupQueryUseCase;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class BootstrapService {
    private final PopupDetailService popupDetailService;

    private final UserQueryUseCase userQueryUseCase;
    private final AlarmQueryUseCase alarmQueryUseCase;
    private final PopupQueryUseCase popupQueryUseCase;

    private final HeaderUtil headerUtil;

    @Transactional(readOnly = true)
    public BootstrapDto bootstrap(HttpServletRequest request) {
        Long userId = headerUtil.parseUserId(request);
        if (userId != null && !userQueryUseCase.existsById(userId)) {
            throw new CommonException(ErrorCode.ACCESS_DENIED_ERROR);
        }

        if (userId != null) { // 로그인 요청일 경우
            // 인기 top5 조회
            List<PopupStoreDto> popularTop5PopupStores = popupDetailService.getPopupStoreDtos(
                    popupQueryUseCase.findHotPopupList(userId),
                    userId
            );
            // 새로오픈팝업 top5 조회
            List<PopupStoreDto> newlyOpenedPopupStores = popupDetailService.getPopupStoreDtos(
                    popupQueryUseCase.findNewPopupList(userId),
                    userId
            );
            // 종료임박팝업 top5 조회
            List<PopupStoreDto> closingSoonPopupStores = popupDetailService.getPopupStoreDtos(
                    popupQueryUseCase.findClosingPopupList(userId),
                    userId
            );

            // 취향 저격 팝업 조회
            List<Popup> recommendPopup = popupQueryUseCase.findRecommandPopupList(userId);
            List<PopupStoreDto> recommendedPopupStores = popupDetailService.getPopupStoreDtos(recommendPopup, userId);

            // 관심 저장 팝업 조회
            User user = userQueryUseCase.findUserById(userId);

            Set<Interest> interest = user.getInterest();
            List<Popup> interestedPopup = interest.stream()
                    .map(Interest::getPopup)
                    .toList();

            List<PopupStoreDto> interestedPopupStores = popupDetailService.getPopupStoreDtos(interestedPopup, userId);

            // 공지 조회
            List<InformAlarm> informAlarms = alarmQueryUseCase.getInformAlarms(userId);

            List<NoticeDto> notice = NoticeDto.fromEntities(informAlarms);

            return BootstrapDto.builder()
                    .popularTop5PopupStores(popularTop5PopupStores)
                    .newlyOpenedPopupStores(newlyOpenedPopupStores)
                    .closingSoonPopupStores(closingSoonPopupStores)
                    .interestedPopupStores(interestedPopupStores)
                    .recommendedPopupStores(recommendedPopupStores)
                    .notices(notice)
                    .build();
        } else { // 비로그인 요청일 경우 유저 관련 로직 생략
            // 인기 top5 조회
            List<PopupStoreDto> popularTop5PopupStores = popupDetailService.guestGetPopupStoreDtos(
                    popupQueryUseCase.findHotPopupList()
            );
            // 새로오픈팝업 top5 조회
            List<PopupStoreDto> newlyOpenedPopupStores = popupDetailService.guestGetPopupStoreDtos(
                    popupQueryUseCase.findNewPopupList()
            );
            // 종료임박팝업 top5 조회
            List<PopupStoreDto> closingSoonPopupStores = popupDetailService.guestGetPopupStoreDtos(
                    popupQueryUseCase.findClosingPopupList()
            );

            return BootstrapDto.builder()
                    .popularTop5PopupStores(popularTop5PopupStores)
                    .newlyOpenedPopupStores(newlyOpenedPopupStores)
                    .closingSoonPopupStores(closingSoonPopupStores)
                    .interestedPopupStores(null)
                    .recommendedPopupStores(null)
                    .notices(null)
                    .build();
        }

    } // 부트스트랩 로딩 api
}
