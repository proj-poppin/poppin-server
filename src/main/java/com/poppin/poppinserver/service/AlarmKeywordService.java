package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.dto.alarm.response.AlarmKeywordResponseDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmKeywordService {

    private final UserRepository userRepository;

    public List<AlarmKeywordResponseDto> readAlarmKeywords(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));


    }
}
