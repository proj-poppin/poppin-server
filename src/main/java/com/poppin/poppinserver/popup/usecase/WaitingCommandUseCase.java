package com.poppin.poppinserver.popup.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.popup.domain.Waiting;

@UseCase
public interface WaitingCommandUseCase {

    Waiting save(Waiting waiting);
}
