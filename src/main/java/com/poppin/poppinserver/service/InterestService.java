package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.interest.request.InterestRequestDto;
import com.poppin.poppinserver.dto.interest.response.InterestDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.*;
import com.poppin.poppinserver.type.EPopupTopic;
import jakarta.transaction.Transactional;
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
    @Transactional // 쿼리 5번 날라감. 최적화 필요
    public InterestDto userAddInterest(Long userId, InterestRequestDto requestDto){
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
        String fcmToken = requestDto.fcmToken();

        fcmTokenService.fcmAddTopic(fcmToken, popup, EPopupTopic.MAGAM);
        fcmTokenService.fcmAddTopic(fcmToken, popup, EPopupTopic.OPEN);
        fcmTokenService.fcmAddTopic(fcmToken, popup, EPopupTopic.CHANGE_INFO);

        return InterestDto.fromEntity(interest,user,popup);
    }

    public Boolean removeInterest(Long userId,InterestRequestDto requestDto){
        Interest interest = interestRepository.findByUserIdAndPopupId(userId, requestDto.popupId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));

        interestRepository.delete(interest);

        /*FCM 구독취소*/
        Popup popup = popupRepository.findById(requestDto.popupId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        String fcmToken = requestDto.fcmToken();

        fcmTokenService.fcmRemoveTopic(fcmToken,popup, EPopupTopic.MAGAM);
        fcmTokenService.fcmRemoveTopic(fcmToken,popup, EPopupTopic.OPEN);
        fcmTokenService.fcmRemoveTopic(fcmToken,popup, EPopupTopic.CHANGE_INFO);
        return true;
    }
}
