package com.poppin.poppinserver.alarm.service;

import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.alarm.dto.alarm.response.AlarmKeywordResponseDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmKeywordService {

    private final UserRepository userRepository;

//    public List<AlarmKeywordResponseDto> readAlarmKeywords(Long userId) {
//        User user = userRepository.findByUserId(userId)
//                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
//
//
//    }
}
