package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.core.util.RandomNicknameUtil;
import com.poppin.poppinserver.user.dto.user.response.UserNicknameResponseDto;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserQueryRepository userQueryRepository;

    // TODO: 삭제 예정 -> API 통합
//    public UserProfileDto updateUserNickname(Long userId, UpdateUserInfoDto updateUserInfoDto) {
//        User user = userQueryUseCase.findUserById(userId);
//        if (userQueryRepository.findByNickname(updateUserInfoDto.nickname()).isPresent() && (!Objects.equals(userId,
//                user.getId()))) {
//            throw new CommonException(ErrorCode.DUPLICATED_NICKNAME);
//        }
//        user.updateUserNickname(updateUserInfoDto.nickname());
//        userQueryRepository.save(user);
//
//        return UserProfileDto.builder()
//                .provider(user.getProvider())
//                .userImageUrl(user.getProfileImageUrl())
//                .email(user.getEmail())
//                .nickname(user.getNickname())
//                .build();
//    }


    public UserNicknameResponseDto generateRandomNickname() {
        String randomNickname = RandomNicknameUtil.generateRandomNickname();
        return new UserNicknameResponseDto(randomNickname);
    }

}
