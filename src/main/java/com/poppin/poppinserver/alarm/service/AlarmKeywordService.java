package com.poppin.poppinserver.alarm.service;

import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.domain.UserAlarmKeyword;
import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmKeywordRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.response.AlarmKeywordResponseDto;
import com.poppin.poppinserver.alarm.repository.FCMTokenRepository;
import com.poppin.poppinserver.alarm.repository.UserAlarmKeywordRepository;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmKeywordService {
    private final UserQueryRepository userQueryRepository;
    private final UserAlarmKeywordRepository userAlarmKeywordRepository;
    private final FCMTokenRepository fcmTokenRepository;

    @Transactional(readOnly = true)
    public List<AlarmKeywordResponseDto> readAlarmKeywords(Long userId) {
        User user = userQueryRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Set<UserAlarmKeyword> userAlarmKeywords = user.getUserAlarmKeywords();
        return AlarmKeywordResponseDto.fromEntity(userAlarmKeywords);
    }

    @Transactional
    public List<AlarmKeywordResponseDto> createAlarmKeyword(Long userId,
                                                            AlarmKeywordRequestDto alarmKeywordRequestDto) {
        User user = userQueryRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        FCMToken fcmToken = fcmTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_FCM_TOKEN));

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
                .fcmToken(fcmToken.getToken())
                .build();

        // 변경된 UserAlarmKeyword 저장
        userAlarmKeywordRepository.save(newUserAlarmKeyword);

        // 키워드 목록 다시 반환
        Set<UserAlarmKeyword> newUserAlarmKeywords = user.getUserAlarmKeywords();

        return AlarmKeywordResponseDto.fromEntity(newUserAlarmKeywords);
    }

    @Transactional
    public void deleteAlarmKeyword(Long userId, Long keywordId) {
        User user = userQueryRepository.findById(userId)
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
        User user = userQueryRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        UserAlarmKeyword userAlarmKeyword = userAlarmKeywordRepository.findById(keywordId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ALARM_KEYWORD));

        userAlarmKeyword.setAlarmStatus(isOn);

        Set<UserAlarmKeyword> userAlarmKeywordList = user.getUserAlarmKeywords();

        return AlarmKeywordResponseDto.fromEntity(userAlarmKeywordList);
    }
}
