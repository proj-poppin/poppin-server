package com.poppin.poppinserver.inform.service;

import com.poppin.poppinserver.inform.repository.ManagerInformCommandRepository;
import com.poppin.poppinserver.inform.repository.ManagerInformQueryRepository;
import com.poppin.poppinserver.inform.usecase.ManagerInformCommandUseCase;
import com.poppin.poppinserver.popup.domain.Popup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ManagerInformCommandService implements ManagerInformCommandUseCase {
    private final ManagerInformCommandRepository managerInformCommandRepository;

    @Override
    public void deleteAllManagerInformByPopup(Popup popup) {
        managerInformCommandRepository.deleteAllByPopupId(popup);
    }

    @Override
    public void deleteAllManagerInformByUserId(Long userId) {
        managerInformCommandRepository.deleteAllByInformerId(userId);
    }
}
