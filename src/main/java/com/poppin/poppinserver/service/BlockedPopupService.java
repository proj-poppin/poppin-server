package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.BlockedPopup;
import com.poppin.poppinserver.domain.Interest;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.dto.blockedPopup.response.BlockedPopupDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.BlockedPopupRepository;
import com.poppin.poppinserver.repository.InterestRepository;
import com.poppin.poppinserver.repository.PopupRepository;
import com.poppin.poppinserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlockedPopupService {
    private final BlockedPopupRepository blockedPopupRepository;
    private final UserRepository userRepository;
    private final PopupRepository popupRepository;
    private final InterestRepository interestRepository;

    public BlockedPopupDto createBlockedPopup(Long popupId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        // 관심 저장 해제
        Optional<Interest> interest = interestRepository.findByUserIdAndPopupId(userId, popupId);

        interest.ifPresent(interestRepository::delete);

        // 팝업 차단 생성 및 저장
        BlockedPopup blockedPopup = BlockedPopup.builder()
                .userId(user)
                .popupId(popup)
                .build();

        blockedPopup = blockedPopupRepository.save(blockedPopup);

        return BlockedPopupDto.fromEntity(blockedPopup);
    }
}
