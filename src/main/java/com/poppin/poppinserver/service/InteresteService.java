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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InteresteService {
    private final UserRepository userRepository;
    private final PopupRepository popupRepository;
    private final InteresteRepository interesteRepository;

    public InteresteDto userAddIntereste(AddInteresteDto addInteresteDto){
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(addInteresteDto.popupId())
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        Intereste intereste = Intereste.builder()
                .user(user)
                .popup(popup)
                .build();

        interesteRepository.save(intereste);

        user.getInterestes().add(intereste);
        popup.getInterestes().add(intereste);

        return InteresteDto.fromEntity(intereste,user,popup);
    }
}
