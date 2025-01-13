package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.popup.domain.Waiting;
import com.poppin.poppinserver.popup.repository.WaitingRepository;
import com.poppin.poppinserver.popup.usecase.WaitingCommandUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopupWaitingService implements WaitingCommandUseCase {

    private final WaitingRepository waitingRepository;

    @Override
    public Waiting save(Waiting waiting) {
        return waitingRepository.save(waiting);
    }
}
