package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.AlarmSetting;
import com.poppin.poppinserver.dto.alarmSetting.request.AlarmSettingRequestDto;
import com.poppin.poppinserver.dto.alarmSetting.response.AlarmSettingResponseDto;

import com.poppin.poppinserver.repository.AlarmSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmSettingService {

    private final AlarmSettingRepository alarmSettingRepository;

    public AlarmSettingResponseDto updateAlarmSetting(AlarmSettingRequestDto reqDto) {
        AlarmSetting alarmSetting = alarmSettingRepository.findByToken(reqDto.token());

        alarmSettingRepository.delete(alarmSetting); // 삭제

        AlarmSetting newAlarmSetting = new AlarmSetting(
                reqDto.token(),
                reqDto.pushYn(),
                reqDto.pushNightYn(),
                reqDto.hoogiYn(),
                reqDto.openYn(),
                reqDto.magamYn(),
                reqDto.changeInfoYn()
        );
        alarmSettingRepository.save(newAlarmSetting);
        AlarmSettingResponseDto resDto = AlarmSettingResponseDto.fromEntity(
                reqDto.token(),
                reqDto.pushYn(),
                reqDto.pushNightYn(),
                reqDto.hoogiYn(),
                reqDto.openYn(),
                reqDto.magamYn(),
                reqDto.changeInfoYn()
        );
        return resDto;
    }

}
