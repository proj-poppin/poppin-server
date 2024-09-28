package com.poppin.poppinserver.interest.service;

import com.poppin.poppinserver.alarm.service.FCMTokenService;
import com.poppin.poppinserver.interest.dto.interest.request.InterestRequestDto;
import com.poppin.poppinserver.interest.dto.interest.response.InterestDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.interest.domain.Interest;
import com.poppin.poppinserver.interest.repository.InterestRepository;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class InterestService {
    private final UserRepository userRepository;
    private final PopupRepository popupRepository;
    private final InterestRepository interestRepository;

    private final FCMTokenService fcmTokenService;

    @Transactional
    public InterestDto userAddInterest(Long userId, InterestRequestDto requestDto) {
        //중복검사
        interestRepository.findByUserIdAndPopupId(userId, requestDto.popupId())
                .ifPresent(interest -> {
                    throw new CommonException(ErrorCode.DUPLICATED_INTEREST);
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(requestDto.popupId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        Interest interest = Interest.builder()
                .user(user)
                .popup(popup)
                .build();

        interestRepository.save(interest);

        popup.addInterestCnt();

        /*알림 구독*/
//        String fcmToken = requestDto.fcmToken();
//
//        fcmTokenService.fcmAddPopupTopic(fcmToken, popup, EPopupTopic.MAGAM);
//        fcmTokenService.fcmAddPopupTopic(fcmToken, popup, EPopupTopic.OPEN);
//        fcmTokenService.fcmAddPopupTopic(fcmToken, popup, EPopupTopic.CHANGE_INFO);

        return InterestDto.fromEntity(interest, popup);
    }

    public InterestDto removeInterest(Long userId, InterestRequestDto requestDto) {
        Interest interest = interestRepository.findByUserIdAndPopupId(userId, requestDto.popupId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));

        Popup popup = popupRepository.findById(requestDto.popupId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        InterestDto interestDto = InterestDto.fromEntity(interest, popup);

        interestRepository.delete(interest);

        /*FCM 구독취소*/
        String fcmToken = requestDto.fcmToken();

        fcmTokenService.fcmRemovePopupTopic(fcmToken, popup, EPopupTopic.MAGAM);
        fcmTokenService.fcmRemovePopupTopic(fcmToken, popup, EPopupTopic.OPEN);
        fcmTokenService.fcmRemovePopupTopic(fcmToken, popup, EPopupTopic.CHANGE_INFO);

        return interestDto;
    }
}
