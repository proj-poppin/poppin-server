package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.BlockedPopup;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.dto.blockedPopup.request.CreateBlockedPopup;
import com.poppin.poppinserver.dto.blockedPopup.response.BlockedPopupDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.BlockedPopupRepository;
import com.poppin.poppinserver.repository.PopupRepository;
import com.poppin.poppinserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlockedPopupService {
    private final BlockedPopupRepository blockedPopupRepository;
    private final UserRepository userRepository;
    private final PopupRepository popupRepository;

    public BlockedPopupDto createBlockedPopup(Long popupId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        BlockedPopup blockedPopup = BlockedPopup.builder()
                                        .userId(user)
                                        .popupId(popup)
                                        .build();

        blockedPopup = blockedPopupRepository.save(blockedPopup);

        return BlockedPopupDto.fromEntity(blockedPopup);
    }
}
