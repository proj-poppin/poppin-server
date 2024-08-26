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
                    return UserAlarmKeyword.builder()
                            .userId(user)
                            .keywordList(new ArrayList<>())
                            .build();
                }).getKeywords();
        return alarmKeywordList.stream()
                .map(AlarmKeywordResponseDto::fromEntity)
                .toList();
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
            throw new CommonException(ErrorCode.DUPLICATED_ALARM_KEYWORD);
        }

        AlarmKeyword newAlarmKeyword = AlarmKeyword.builder()
                .userAlarmKeyword(userAlarmKeyword)
                .keyword(alarmKeywordRequestDto.keyword())
                .build();

        // UserAlarmKeyword의 키워드 리스트에 추가
        userAlarmKeyword.getKeywords().add(newAlarmKeyword);

        // 변경된 UserAlarmKeyword 저장
        userAlarmKeywordRepository.save(userAlarmKeyword);

        // 키워드 목록 다시 반환
        List<AlarmKeyword> alarmKeywordList = userAlarmKeyword.getKeywords();

        return alarmKeywordList.stream()
                .map(AlarmKeywordResponseDto::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteAlarmKeyword(Long userId, Long keywordId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        UserAlarmKeyword userAlarmKeyword = userAlarmKeywordRepository.findByUserId(user)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));

        AlarmKeyword alarmKeyword = alarmKeywordRepository.findById(keywordId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ALARM_KEYWORD));

        // 알람 키워드가 유저의 알람 키워드 리스트에 포함되는지 확인
        if (!userAlarmKeyword.getKeywords().contains(alarmKeyword)) {
            throw new CommonException(ErrorCode.NOT_FOUND_ALARM_KEYWORD);
        }

        // 키워드 리스트에서 제거
        userAlarmKeyword.getKeywords().remove(alarmKeyword);

        // 키워드 삭제
        alarmKeywordRepository.delete(alarmKeyword);

        // 응답 새로 내려줄지 말지 클라와 논의 후 작성
        // 현재까지는 그냥 삭제완료 String만 json으로 반환
    }
}
