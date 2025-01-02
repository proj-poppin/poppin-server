package com.poppin.poppinserver.admin.service;

import com.poppin.poppinserver.admin.dto.request.AdminFaqRequestDto;
import com.poppin.poppinserver.admin.dto.response.AdminFaqResponseDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.user.domain.FreqQuestion;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.FreqQuestionRepository;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminFAQService {
    private final FreqQuestionRepository freqQuestionRepository;
    private final UserQueryRepository userQueryRepository;

    public List<AdminFaqResponseDto> readFAQs() {
        List<FreqQuestion> freqQuestionList = freqQuestionRepository.findAllByOrderByCreatedAtDesc();
        List<AdminFaqResponseDto> faqDtoList = new ArrayList<>();
        for (FreqQuestion freqQuestion : freqQuestionList) {
            faqDtoList.add(AdminFaqResponseDto.builder()
                    .faqId(freqQuestion.getId())
                    .question(freqQuestion.getQuestion())
                    .answer(freqQuestion.getAnswer())
                    .createdAt(freqQuestion.getCreatedAt().toString())
                    .build());
        }
        return faqDtoList;
    }

    public AdminFaqResponseDto createFAQ(Long adminId, AdminFaqRequestDto adminFaqRequestDto) {
        User admin = userQueryRepository.findById(adminId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        FreqQuestion freqQuestion = FreqQuestion.builder()
                .adminId(admin)
                .question(adminFaqRequestDto.question())
                .answer(adminFaqRequestDto.answer())
                .createdAt(LocalDateTime.now())
                .build();
        freqQuestionRepository.save(freqQuestion);
        return AdminFaqResponseDto.builder()
                .faqId(freqQuestion.getId())
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
