package com.poppin.poppinserver.user.controller;

import com.poppin.poppinserver.alarm.dto.alarm.request.NotificationRequestDto;
import com.poppin.poppinserver.alarm.service.AlarmService;
import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.core.type.EOperationStatus;
import com.poppin.poppinserver.core.type.EPopupSort;
import com.poppin.poppinserver.popup.dto.popup.response.PopupStoreDto;
import com.poppin.poppinserver.popup.service.SearchPopupService;
import com.poppin.poppinserver.user.controller.swagger.SwaggerUserController;
import com.poppin.poppinserver.user.dto.user.request.CreateUserTasteDto;
import com.poppin.poppinserver.user.dto.user.response.UserFaqResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserNicknameResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserPreferenceUpdateResponseDto;
import com.poppin.poppinserver.user.service.*;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController implements SwaggerUserController {
    private final AlarmService alarmService;
    private final UserService userService;
    private final SearchPopupService searchPopupService;
    private final BlockUserService blockUserService;
    private final UserPreferenceSettingService userPreferenceSettingService;
    private final UserProfileImageService userProfileImageService;
    private final UserHardDeleteService userHardDeleteService;
    private final UserFaqService userFaqService;

    @PutMapping("/popup-taste")
    public ResponseDto<UserPreferenceUpdateResponseDto> updateUserTaste(
            @UserId Long userId,
            @RequestBody @Valid CreateUserTasteDto userTasteDto
    ) {
        return ResponseDto.ok(userPreferenceSettingService.updateUserPreference(userId, userTasteDto));
    }

//    @PostMapping("/image")
//    public ResponseDto<String> createUserProfileImage(@UserId Long userId,
//                                                      @RequestPart(value = "profileImage") MultipartFile profileImage) {
//        return ResponseDto.created(userProfileImageService.createProfileImage(userId, profileImage));
//    }

    @PatchMapping(value = "/profile", consumes = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE
    })
    public ResponseDto<String> updateUserProfile(
            @UserId Long userId,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "nickname") String nickname
    ) {
        log.info("profileImage: {}", profileImage);
        return ResponseDto.ok(userProfileImageService.updateProfile(userId, profileImage, nickname));
    }

    @DeleteMapping("/image")
    public ResponseDto<String> deleteUserProfileImage(@UserId Long userId) {
        userProfileImageService.deleteProfileImage(userId);
        return ResponseDto.ok("프로필 이미지가 삭제되었습니다.");
    }

//    @PutMapping("/settings")
//    public ResponseDto<UserProfileDto> updateUserNickname(
//            @UserId Long userId,
//            @RequestBody UpdateUserInfoDto updateUserInfoDto
//    ) {
//        return ResponseDto.ok(userService.updateUserNickname(userId, updateUserInfoDto));
//    }

    @DeleteMapping("/withdrawal")
    public ResponseDto<String> deleteUser(@UserId Long userId) {
        userHardDeleteService.deleteUser(userId);
        return ResponseDto.ok("회원 탈퇴가 완료되었습니다.");
    }


    /*마이페이지 - 일반후기 팝업 검색*/
    @GetMapping("/popup/search")
    public ResponseDto<PagingResponseDto<List<PopupStoreDto>>> searchPopupName(@RequestParam("text") String text,
                                                                               @RequestParam("taste") String taste,
                                                                               @RequestParam("prepered") String prepered,
                                                                               @RequestParam("oper") EOperationStatus oper,
                                                                               @RequestParam("order") EPopupSort order,
                                                                               @RequestParam("page") int page,
                                                                               @RequestParam("size") int size,
                                                                               HttpServletRequest request) {
        return ResponseDto.ok(
                searchPopupService.readSearchingList(text, taste, prepered, oper, order, page, size, request));
    }

    /*마이페이지 - 자주 묻는 질문 조회*/
    @GetMapping("/support/faqs")
    public ResponseDto<List<UserFaqResponseDto>> readFAQs() {
        return ResponseDto.ok(userFaqService.readFAQs());
    }

    /*마이페이지 - 한글 닉네임 랜덤 생성*/
    @GetMapping("/random-nickname")
    public ResponseDto<UserNicknameResponseDto> generateRandomNickname() {
        return ResponseDto.ok(userService.generateRandomNickname());
    }

    @PostMapping("/block/{blockUserId}")
    public ResponseDto<String> createBlockedUser(@UserId Long userId, @PathVariable Long blockUserId) {
        blockUserService.createBlockedUser(userId, blockUserId);
        return ResponseDto.ok("차단 완료되었습니다.");
    }

    @PatchMapping("/notifications/check")
    public ResponseDto<String> checkNotification(@UserId Long userId, @RequestBody NotificationRequestDto notificationRequestDto){
        return ResponseDto.ok(alarmService.checkNotification(userId, notificationRequestDto));
    }
}
