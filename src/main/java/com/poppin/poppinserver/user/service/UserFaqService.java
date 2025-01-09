package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.admin.domain.FreqQuestion;
import com.poppin.poppinserver.admin.repository.FreqQuestionRepository;
import com.poppin.poppinserver.user.dto.user.response.UserFaqResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFaqService {
    private final FreqQuestionRepository freqQuestionRepository;

    public List<UserFaqResponseDto> readFAQs() {
        List<FreqQuestion> freqQuestionList = freqQuestionRepository.findAllByOrderByCreatedAtDesc();
        return freqQuestionList.stream()
                .map(UserFaqResponseDto::fromFaqEntity)
                .toList();
    }
}
