package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.util.RandomNicknameUtil;
import com.poppin.poppinserver.user.domain.FreqQuestion;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.dto.faq.response.UserFaqResponseDto;
import com.poppin.poppinserver.user.dto.user.request.UpdateUserInfoDto;
import com.poppin.poppinserver.user.dto.user.response.UserNicknameResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserProfileDto;
import com.poppin.poppinserver.user.repository.FreqQuestionRepository;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserQueryUseCase userQueryUseCase;
    private final UserQueryRepository userQueryRepository;
    private final FreqQuestionRepository freqQuestionRepository;

    public UserProfileDto updateUserNickname(Long userId, UpdateUserInfoDto updateUserInfoDto) {
        User user = userQueryUseCase.findUserById(userId);
        if (userQueryRepository.findByNickname(updateUserInfoDto.nickname()).isPresent() && (!Objects.equals(userId,
                user.getId()))) {
            throw new CommonException(ErrorCode.DUPLICATED_NICKNAME);
        }
        user.updateUserNickname(updateUserInfoDto.nickname());
        userQueryRepository.save(user);

        return UserProfileDto.builder()
                .provider(user.getProvider())
                .userImageUrl(user.getProfileImageUrl())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    public List<UserFaqResponseDto> readFAQs() {
        List<FreqQuestion> freqQuestionList = freqQuestionRepository.findAllByOrderByCreatedAtDesc();
        List<UserFaqResponseDto> faqDtoList = new ArrayList<>();
        for (FreqQuestion freqQuestion : freqQuestionList) {
            faqDtoList.add(UserFaqResponseDto.builder()
                    .faqId(String.valueOf(freqQuestion.getId()))
                    .question(freqQuestion.getQuestion())
                    .answer(freqQuestion.getAnswer())
                    .createdAt(freqQuestion.getCreatedAt().toString())
                    .build());
        }
        return faqDtoList;
    }

    public UserNicknameResponseDto generateRandomNickname() {
        String randomNickname = RandomNicknameUtil.generateRandomNickname();
        return new UserNicknameResponseDto(randomNickname);
    }

    public void addReviewCnt(User user) {
        user.addReviewCnt();
        userQueryRepository.save(user);
    }
}
