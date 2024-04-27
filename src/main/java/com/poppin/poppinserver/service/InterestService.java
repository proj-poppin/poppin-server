package com.poppin.poppinserver.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.interest.requeste.AddInterestDto;
import com.poppin.poppinserver.dto.interest.response.InterestDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.*;
import com.poppin.poppinserver.util.NotificationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InterestService {
    private final UserRepository userRepository;
    private final PopupRepository popupRepository;
    private final InterestRepository interestRepository;
    private final NotificationTokenRepository notificationTokenRepository;
    private final NotificationTopicRepository notificationTopicRepository;

    private final NotificationUtil notificationUtil;

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

        /*
          Method : 관심 팝업 등록 시 주제 테이블에 데이터 삽입 , 구독 시키기
          Author : sakang
          Date   : 2024-04-27
        */
        try {
            log.info("==== subscribe topic START ====");
            List<NotificationToken> tokenList = notificationTokenRepository.findTokenListByUserId(user.getId());
            if (tokenList.isEmpty())throw new CommonException(ErrorCode.NOT_FOUND_TOKEN);

            notificationUtil.androidSubscribeTopic(tokenList,popup); // 구독 및 저장

        }catch (CommonException | FirebaseMessagingException e){
            log.error("==== subscribe topic FAILED ====");
            e.printStackTrace();
        }

        return InterestDto.fromEntity(interest,user,popup);
    }

    public Boolean removeInterest(Long userId, Long popupId) {
        Interest interest = interestRepository.findByUserIdAndPopupId(userId, popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));

        interestRepository.delete(interest);

        return true;
    }
}
