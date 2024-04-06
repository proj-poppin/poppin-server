package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.user.request.CreateUserTasteDto;
import com.poppin.poppinserver.dto.user.request.UserInfoDto;
import com.poppin.poppinserver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/popup-taste")
    public ResponseDto<?> createUserTaste(
            @UserId Long userId,
            @RequestBody @Valid CreateUserTasteDto userTasteDto
    ) {
        return ResponseDto.created(userService.createUserTaste(userId, userTasteDto));
    }

    @GetMapping("/popup-taste")
    public ResponseDto<?> readUserTaste(@UserId Long userId) {
        return ResponseDto.ok(userService.readUserTaste(userId));
    }

    @PutMapping("/popup-taste")
    public ResponseDto<?> updateUserTaste(
            @UserId Long userId,
            @RequestBody @Valid CreateUserTasteDto userTasteDto
    ) {
        return ResponseDto.ok(userService.updateUserTaste(userId, userTasteDto));
    }

    @GetMapping("")
    public ResponseDto<?> readUserProfile(@UserId Long userId) {
        return ResponseDto.ok(userService.readUserProfile(userId));
    }

    @PostMapping("/image")
    public ResponseDto<?> createUserProfileImage(@UserId Long userId, @RequestPart(value = "profileImage") MultipartFile profileImage) {
        return ResponseDto.ok(userService.createProfileImage(userId, profileImage));
    }

    @PutMapping("/image")
    public ResponseDto<?> updateUserProfileImage(@UserId Long userId, @RequestParam(value = "profileImage") MultipartFile profileImage) {
        return ResponseDto.ok(userService.updateProfileImage(userId, profileImage));
    }

    @DeleteMapping("/image")
    public ResponseDto<?> deleteUserProfileImage(@UserId Long userId) {
        userService.deleteProfileImage(userId);
        return ResponseDto.ok("프로필 이미지가 삭제되었습니다.");
    }

    @PatchMapping("")
    public ResponseDto<?> updateUserNicknameAndBirthDate(
            @UserId Long userId,
            @RequestBody UserInfoDto userInfoDto
    ) {
        return ResponseDto.ok(userService.updateUserNicknameAndBirthDate(userId, userInfoDto));
    }

    @DeleteMapping("/withdrawal")
    public ResponseDto<?> deleteUser(@UserId Long userId) {
        userService.deleteUser(userId);
        return ResponseDto.ok("회원 탈퇴가 완료되었습니다.");
    }
}
