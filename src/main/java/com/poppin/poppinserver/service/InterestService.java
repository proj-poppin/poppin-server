package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.Interest;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.dto.interest.requeste.AddInterestDto;
import com.poppin.poppinserver.dto.interest.response.InterestDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.InterestRepository;
import com.poppin.poppinserver.repository.PopupRepository;
import com.poppin.poppinserver.repository.UserRepository;
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

    @Transactional // 쿼리 5번 날라감. 최적화 필요
    public InterestDto userAddInterest(AddInterestDto addInterestDto, Long userId){
        //중복검사
        interestRepository.findByUserIdAndPopupId(userId, addInterestDto.popupId())
                .ifPresent(interest -> {
                    throw new CommonException(ErrorCode.DUPLICATED_INTERESTE);
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

        return InterestDto.fromEntity(interest,user,popup);
    }
}
