//package com.poppin.poppinserver.legacy.alarm.service;
//
//import com.poppin.poppinserver.alarm.domain.AlarmSetting;
//import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmTokenRequestDto;
//import com.poppin.poppinserver.alarm.repository.AlarmSettingRepository;
//import com.poppin.poppinserver.legacy.alarm.dto.SettingResponseDto;
//import com.poppin.poppinserver.user.domain.User;
//import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class AlarmSettingService {
//    private final AlarmSettingRepository alarmSettingRepository;
//    private final UserQueryUseCase userQueryUseCase;
//
//    // TODO: Legacy 삭제 예정
//    public SettingResponseDto readAlarmSetting(Long userId, AlarmTokenRequestDto reqDto) {
//        User user = userQueryUseCase.findUserById(userId);
//
//        AlarmSetting setting = alarmSettingRepository.findByToken(reqDto.fcmToken());
//
//        SettingResponseDto resDto = SettingResponseDto.fromEntity(setting);
//
//        return resDto;
//    }
//}
