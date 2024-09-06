package com.poppin.poppinserver.alarm.service;

import com.poppin.poppinserver.alarm.domain.UserAlarmKeyword;
import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmKeywordRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.response.AlarmKeywordResponseDto;
import com.poppin.poppinserver.alarm.repository.UserAlarmKeywordRepository;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmKeywordService {
    private final UserRepository userRepository;
    private final UserAlarmKeywordRepository userAlarmKeywordRepository;

    @Transactional(readOnly = true)
    public List<AlarmKeywordResponseDto> readAlarmKeywords(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Set<UserAlarmKeyword> userAlarmKeywords = user.getUserAlarmKeywords();
        return AlarmKeywordResponseDto.fromEntity(userAlarmKeywords);
    }

    @Transactional
    public List<AlarmKeywordResponseDto> createAlarmKeyword(Long userId, AlarmKeywordRequestDto alarmKeywordRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Set<UserAlarmKeyword> userAlarmKeywords = userAlarmKeywordRepository.findAllByUser(user);
        String newKeyword = alarmKeywordRequestDto.keyword();

        // userAlarmKeywords에 새로운 키워드가 존재하는지 확인
        boolean keywordExists = userAlarmKeywords.stream()
                .anyMatch(userAlarmKeyword -> userAlarmKeyword.getKeyword().equals(newKeyword));

        if (keywordExists) {
            throw new CommonException(ErrorCode.DUPLICATED_ALARM_KEYWORD);
        }

        UserAlarmKeyword newUserAlarmKeyword = UserAlarmKeyword.builder()
                .user(user)
                .keyword(newKeyword)
                .fcmToken(alarmKeywordRequestDto.fcmToken())
                .build();

        // 변경된 UserAlarmKeyword 저장
        userAlarmKeywordRepository.save(newUserAlarmKeyword);

        // 키워드 목록 다시 반환
        Set<UserAlarmKeyword> newUserAlarmKeywords = user.getUserAlarmKeywords();

        return AlarmKeywordResponseDto.fromEntity(newUserAlarmKeywords);
    }

    @Transactional
    public void deleteAlarmKeyword(Long userId, Long keywordId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        UserAlarmKeyword userAlarmKeyword = userAlarmKeywordRepository.findById(keywordId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ALARM_KEYWORD));

        // 키워드 삭제
        userAlarmKeywordRepository.delete(userAlarmKeyword);

//        Set<UserAlarmKeyword> userAlarmKeywordList = user.getUserAlarmKeywords();
//        return AlarmKeywordResponseDto.fromEntity(userAlarmKeywordList);
    }

    // 알람 키워드 상태 변경
    @Transactional
    public List<AlarmKeywordResponseDto> setAlarmKeywordStatus(Long userId, Long keywordId, Boolean isOn) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        UserAlarmKeyword userAlarmKeyword = userAlarmKeywordRepository.findById(keywordId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ALARM_KEYWORD));

        userAlarmKeyword.setAlarmStatus(isOn);

        Set<UserAlarmKeyword> userAlarmKeywordList = user.getUserAlarmKeywords();

        return AlarmKeywordResponseDto.fromEntity(userAlarmKeywordList);
    }
}
