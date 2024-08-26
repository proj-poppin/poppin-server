package com.poppin.poppinserver.alarm.service;

import com.poppin.poppinserver.alarm.domain.AlarmKeyword;
import com.poppin.poppinserver.alarm.domain.UserAlarmKeyword;
import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmKeywordRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.response.AlarmKeywordResponseDto;
import com.poppin.poppinserver.alarm.repository.AlarmKeywordRepository;
import com.poppin.poppinserver.alarm.repository.UserAlarmKeywordRepository;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmKeywordService {
    private final UserRepository userRepository;
    private final AlarmKeywordRepository alarmKeywordRepository;
    private final UserAlarmKeywordRepository userAlarmKeywordRepository;

    @Transactional(readOnly = true)
    public List<AlarmKeywordResponseDto> readAlarmKeywords(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        List<AlarmKeyword> alarmKeywordList = userAlarmKeywordRepository.findByUserId(user)
                .orElseGet(() -> {
                    // 최초 진입 시 새로운 UserAlarmKeyword 를 생성
                    UserAlarmKeyword newUserAlarmKeyword = UserAlarmKeyword.builder()
                            .userId(user)
                            .keywordList(new ArrayList<>())
                            .build();
                    return newUserAlarmKeyword;
                }).getKeywords();
        List<AlarmKeywordResponseDto> alarmKeywordResponseDtoList = alarmKeywordList.stream()
                .map(alarmKeyword -> AlarmKeywordResponseDto.fromEntity(alarmKeyword))
                .toList();
        return alarmKeywordResponseDtoList;
    }

    @Transactional
    public List<AlarmKeywordResponseDto> createAlarmKeyword(Long userId, AlarmKeywordRequestDto alarmKeywordRequestDto) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        UserAlarmKeyword userAlarmKeyword = userAlarmKeywordRepository.findByUserId(user)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));

        // 중복 키워드 생성 방지 로직
        boolean keywordExists = alarmKeywordRepository
                .findByUserAlarmKeywordAndKeyword(userAlarmKeyword, alarmKeywordRequestDto.keyword())
                .isPresent();

        if (keywordExists) {
            throw new CommonException(ErrorCode.DUPLICATED_ALARM_KEYWORD));
        }

        AlarmKeyword newAlarmKeyword = AlarmKeyword.builder()
                .userAlarmKeyword(userAlarmKeyword)
                .keyword(alarmKeywordRequestDto.keyword())
                .build();

        // UserAlarmKeyword의 키워드 리스트에 추가
        userAlarmKeyword.getKeywords().add(newAlarmKeyword);

        // 변경된 UserAlarmKeyword를 저장 (필요 시)
        userAlarmKeywordRepository.save(userAlarmKeyword);

        // 키워드 목록 다시 반환
        List<AlarmKeyword> alarmKeywordList = userAlarmKeyword.getKeywords();
        List<AlarmKeywordResponseDto> alarmKeywordResponseDtoList = alarmKeywordList.stream()
                .map(keyword -> AlarmKeywordResponseDto.fromEntity(keyword))
                .toList();
        return alarmKeywordResponseDtoList;
    }
}
