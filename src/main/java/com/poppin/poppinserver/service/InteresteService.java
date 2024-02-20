package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.Intereste;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.dto.Intereste.requeste.AddInteresteDto;
import com.poppin.poppinserver.dto.Intereste.response.InteresteDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.InteresteRepository;
import com.poppin.poppinserver.repository.PopupRepository;
import com.poppin.poppinserver.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InteresteService {
    private final UserRepository userRepository;
    private final PopupRepository popupRepository;
    private final InteresteRepository interesteRepository;

    @Transactional // 쿼리 5번 날라감. 최적화 필요
    public InteresteDto userAddIntereste(AddInteresteDto addInteresteDto){
        //중복검사
        interesteRepository.findByUserIdAndPopupId(1L, addInteresteDto.popupId())
                .ifPresent(intereste -> {
                    throw new CommonException(ErrorCode.DUPLICATED_INTERESTE);
                });

        User user = userRepository.findById(1L)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(addInteresteDto.popupId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        Intereste intereste = Intereste.builder()
                .user(user)
                .popup(popup)
                .build();

        interesteRepository.save(intereste);

        user.addIntereste(intereste);
        popup.addIntereste(intereste);
        popup.addInteresteCnt();

        return InteresteDto.fromEntity(intereste,user,popup);
    }
}
