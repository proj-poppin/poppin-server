package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.FreqQuestion;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.dto.faq.request.FaqRequestDto;
import com.poppin.poppinserver.dto.faq.response.FaqResponseDto;
import com.poppin.poppinserver.dto.user.response.UserAdministrationDetailDto;
import com.poppin.poppinserver.dto.user.response.UserAdministrationDto;
import com.poppin.poppinserver.dto.user.response.UserListDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.FreqQuestionRepository;
import com.poppin.poppinserver.repository.ReviewRepository;
import com.poppin.poppinserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {
    private final FreqQuestionRepository freqQuestionRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

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

    public UserListDto readUsers(String sortField, String sortOrder) {
        Sort sort = getSort(sortField, sortOrder);
        List<User> userList = userRepository.findAll(sort);
        List<UserAdministrationDto> userAdministrationDtoList = userList.stream()
                .map(user -> UserAdministrationDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .requiresSpecialCare(user.getRequiresSpecialCare())
                        .build())
                .collect(Collectors.toList());
        int userCnt = userAdministrationDtoList.size();
        return UserListDto.builder()
                .userList(userAdministrationDtoList)
                .userCnt(userCnt)
                .build();
    }

    private Sort getSort(String sortField, String sortOrder) {
        Sort.Direction dir = Sort.Direction.ASC;
        if (sortOrder.equalsIgnoreCase("desc")) {
            dir = Sort.Direction.DESC;
        }

        switch (sortField) {
            case "createdAt":
                return Sort.by(dir, "createdAt");
            default:
                return Sort.by(dir, "nickname");
        }
    }

    public UserAdministrationDetailDto readUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Long hiddenReviewCount = reviewRepository.countByUserIdAndIsVisibleFalse(userId);
        return UserAdministrationDetailDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userImageUrl(user.getProfileImageUrl())
                .nickname(user.getNickname())
                .provider(user.getProvider())
                .requiresSpecialCare(user.getRequiresSpecialCare())
                .hiddenReviewCount(hiddenReviewCount)
                .build();
    }

    public UserListDto searchUsers(String text) {
        List<User> userList = userRepository.findByNicknameContainingOrEmailContainingOrderByNickname(text, text);
        List<UserAdministrationDto> userAdministrationDtoList = new ArrayList<>();
        for (User user : userList) {
            userAdministrationDtoList.add(UserAdministrationDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .requiresSpecialCare(user.getRequiresSpecialCare())
                    .build());
        }
        int userCnt = userAdministrationDtoList.size();
        return UserListDto.builder()
                .userList(userAdministrationDtoList)
                .userCnt(userCnt)
                .build();
    }
}
