package com.poppin.poppinserver.visit.service;

import com.poppin.poppinserver.core.type.ESatisfaction;
import com.poppin.poppinserver.core.type.EVisitDate;
import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataInfoDto;
import com.poppin.poppinserver.visit.repository.VisitorDataRepository;
import com.poppin.poppinserver.visit.usecase.VisitorDataQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class VisitorDataQueryService implements VisitorDataQueryUseCase {

    private final VisitorDataRepository visitorDataRepository;

    @Override
    public VisitorDataInfoDto findVisitorData(Long popupId) {
        Map<String, Object> weekdayAm = checkAndInitialize(
                visitorDataRepository.findCongestionRatioByPopupId(popupId, EVisitDate.fromValue("평일 오전").toString()));
        Map<String, Object> weekdayPm = checkAndInitialize(
                visitorDataRepository.findCongestionRatioByPopupId(popupId, EVisitDate.fromValue("평일 오후").toString()));
        Map<String, Object> weekendAm = checkAndInitialize(
                visitorDataRepository.findCongestionRatioByPopupId(popupId, EVisitDate.fromValue("주말 오전").toString()));
        Map<String, Object> weekendPm = checkAndInitialize(
                visitorDataRepository.findCongestionRatioByPopupId(popupId, EVisitDate.fromValue("주말 오후").toString()));
        Optional<Integer> satisfaction = visitorDataRepository.satisfactionRate(popupId,
                ESatisfaction.fromValue("만족").toString());

        VisitorDataInfoDto visitorDataDto = VisitorDataInfoDto.fromEntity(weekdayAm, weekdayPm, weekendAm, weekendPm,
                satisfaction);

        return visitorDataDto;
    }

    @Override
    public Map<String, Object> checkAndInitialize(Map<String, Object> result) {
        if (result == null) {
            result = new HashMap<>();
            result.put("congestionRate", "여유");  // 기본 값 설정
            result.put("congestionRatio", 0);
        } else {
            if (result.get("congestionRate") == null) {
                result.put("congestionRate", "여유");  // 기본 값 설정
            }
            if (result.get("congestionRatio") == null) {
                result.put("congestionRatio", 0);
            }
        }
        return result;
    }


}
