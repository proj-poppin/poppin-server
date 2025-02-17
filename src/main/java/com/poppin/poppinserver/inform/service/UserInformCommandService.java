package com.poppin.poppinserver.inform.service;

import com.poppin.poppinserver.inform.repository.UserInformRepository;
import com.poppin.poppinserver.inform.usecase.UserInformCommandUseCase;
import com.poppin.poppinserver.popup.domain.Popup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserInformCommandService implements UserInformCommandUseCase {
    private final UserInformRepository userInformRepository;

    @Override
    public void deleteAllUserInformByPopup(Popup popup) {
        userInformRepository.deleteAllByPopupId(popup);
    }
}
