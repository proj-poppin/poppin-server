package com.poppin.poppinserver.inform.service;

import com.poppin.poppinserver.inform.repository.ManagerInformRepository;
import com.poppin.poppinserver.inform.usecase.ManagerInformCommandUseCase;
import com.poppin.poppinserver.popup.domain.Popup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ManagerInformCommandService implements ManagerInformCommandUseCase {
    private final ManagerInformRepository managerInformRepository;

    @Override
    public void deleteAllManagerInformByPopup(Popup popup) {
        managerInformRepository.deleteAllByPopupId(popup);
    }
}
