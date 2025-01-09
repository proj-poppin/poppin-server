package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.popup.service.S3Service;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserProfileImageService {
    private final UserQueryUseCase userQueryUseCase;
    private final UserQueryRepository userQueryRepository;
    private final S3Service s3Service;

//    public String createProfileImage(Long userId, MultipartFile profileImage) {
//        User user = userQueryUseCase.findUserById(userId);
//        String profileImageUrl = s3Service.uploadUserProfile(profileImage, userId);
//        user.updateProfileImage(profileImageUrl);
//        userQueryRepository.save(user);
//
//        return user.getProfileImageUrl();
//    }

    public String updateProfile(Long userId, MultipartFile profileImage, String nickname) {
        User user = userQueryUseCase.findUserById(userId);
        String userProfileImageUrl = user.getProfileImageUrl();

        if (profileImage != null && userProfileImageUrl != null) {
            String profileImageUrl = s3Service.replaceImage(user.getProfileImageUrl(), profileImage, userId);
            user.updateProfileImage(profileImageUrl);
        }

        if (profileImage != null && userProfileImageUrl == null) {
            String profileImageUrl = s3Service.uploadUserProfile(profileImage, userId);
            user.updateProfileImage(profileImageUrl);
        }

        user.updateUserNickname(nickname);
        userQueryRepository.save(user);

        return user.getProfileImageUrl();
    }

    public void deleteProfileImage(Long userId) {
        User user = userQueryUseCase.findUserById(userId);
        s3Service.deleteImage(user.getProfileImageUrl());
        user.deleteProfileImage();
        userQueryRepository.save(user);
    }
}
