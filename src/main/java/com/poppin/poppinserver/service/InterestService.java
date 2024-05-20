package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.interest.requeste.AddInterestDto;
import com.poppin.poppinserver.dto.interest.response.InterestDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class InterestService {
    private final UserRepository userRepository;
    private final PopupRepository popupRepository;
    private final InterestRepository interestRepository;

    private final FCMService fcmService;
    @Transactional // 쿼리 5번 날라감. 최적화 필요
    public InterestDto userAddInterest(AddInterestDto addInterestDto, Long userId){
        //중복검사
        interestRepository.findByUserIdAndPopupId(userId, addInterestDto.popupId())
                .ifPresent(interest -> {
                    throw new CommonException(ErrorCode.DUPLICATED_INTEREST);
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(addInterestDto.popupId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        Interest interest = Interest.builder()
                .user(user)
                .popup(popup)
                .build();

        interestRepository.save(interest);

        popup.addInterestCnt();

//        /*알림 구독*/
//        String token = addInterestDto.fcmToken();
//        List<String> stringList = new ArrayList<>();
//        stringList.add("MG");
//        stringList.add("CI");
//        stringList.add("OP");
//        for (String x : stringList ){
//            fcmService.fcmAddTopic(token, popup, x);
//        }
        return InterestDto.fromEntity(interest,user,popup);
    }

    public Boolean removeInterest(Long userId, Long popupId){//, String fcmToken) {
        Interest interest = interestRepository.findByUserIdAndPopupId(userId, popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));

        interestRepository.delete(interest);

//        /*FCM 구독취소*/
//        List<String> stringList = new ArrayList<>();
//        stringList.add("MG");
//        stringList.add("CI");
//        stringList.add("OP");
//        for (String x : stringList ){
//            fcmService.fcmRemoveTopic(fcmToken, x);
//        }
        return true;
    }
}
