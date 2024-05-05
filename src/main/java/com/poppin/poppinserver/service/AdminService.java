package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.FreqQuestion;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.dto.faq.request.FaqRequestDto;
import com.poppin.poppinserver.dto.faq.response.FaqResponseDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.FreqQuestionRepository;
import com.poppin.poppinserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {
    private final FreqQuestionRepository freqQuestionRepository;
    private final UserRepository userRepository;
    public List<FaqResponseDto> readFAQs() {
        List<FreqQuestion> freqQuestionList = freqQuestionRepository.findAllByOrderByCreatedAtDesc();
        List<FaqResponseDto> faqDtoList = new ArrayList<>();
        for (FreqQuestion freqQuestion : freqQuestionList) {
            faqDtoList.add(FaqResponseDto.builder()
                    .id(freqQuestion.getId())
                    .question(freqQuestion.getQuestion())
                    .answer(freqQuestion.getAnswer())
                    .createdAt(freqQuestion.getCreatedAt().toString())
                    .build());
        }
        return faqDtoList;
    }

    public FaqResponseDto createFAQ(Long adminId, FaqRequestDto faqRequestDto) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        FreqQuestion freqQuestion = FreqQuestion.builder()
                .adminId(admin)
                .question(faqRequestDto.question())
                .answer(faqRequestDto.answer())
                .createdAt(LocalDateTime.now())
                .build();
        freqQuestionRepository.save(freqQuestion);
        return FaqResponseDto.builder()
                .id(freqQuestion.getId())
                .question(freqQuestion.getQuestion())
                .answer(freqQuestion.getAnswer())
                .createdAt(freqQuestion.getCreatedAt().toString())
                .build();
    }

    public void deleteFAQ(Long faqId) {
        FreqQuestion freqQuestion = freqQuestionRepository.findById(faqId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));
        freqQuestionRepository.delete(freqQuestion);
    }
}
