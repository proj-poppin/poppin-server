package com.poppin.poppinserver.visit.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataInfoDto;

import java.util.Map;

@UseCase
public interface VisitorDataQueryUseCase {
    VisitorDataInfoDto findVisitorData(Long popupId);

    Map<String, Object> checkAndInitialize(Map<String, Object> result);


}
