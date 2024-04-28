package com.poppin.poppinserver.service;

import com.poppin.poppinserver.type.ESatisfaction;
import com.poppin.poppinserver.type.EVisitDate;
import com.poppin.poppinserver.dto.visitorData.response.VisitorDataInfoDto;
import com.poppin.poppinserver.repository.VisitorDataRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class VisitorDataService {

    private final VisitorDataRepository visitorDataRepository;

    public VisitorDataInfoDto getVisitorData(Long popupId){

        Map<String,Object> weekdayAm = visitorDataRepository.findCongestionRatioByPopupId(popupId, EVisitDate.fromValue("평일 오전").toString());
        Map<String,Object> weekdayPm = visitorDataRepository.findCongestionRatioByPopupId(popupId, EVisitDate.fromValue("평일 오후").toString());
        Map<String,Object> weekendAm = visitorDataRepository.findCongestionRatioByPopupId(popupId, EVisitDate.fromValue("주말 오전").toString());
        Map<String,Object> weekendPm = visitorDataRepository.findCongestionRatioByPopupId(popupId, EVisitDate.fromValue("주말 오후").toString());
        Optional<Integer> satisfaction = visitorDataRepository.satisfactionRate(popupId, ESatisfaction.fromValue("만족").toString());

        VisitorDataInfoDto visitorDataDto = VisitorDataInfoDto.fromEntity(weekdayAm, weekdayPm, weekendAm, weekendPm, satisfaction);

        return visitorDataDto;
    }
}
