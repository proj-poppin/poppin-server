package com.poppin.poppinserver.user.controller;

import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmSettingRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.request.NotificationRequestDto;
import com.poppin.poppinserver.alarm.service.AlarmService;
import com.poppin.poppinserver.alarm.service.AlarmSettingService;
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
import com.poppin.poppinserver.user.dto.user.response.UserNotificationSettingResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserPreferenceUpdateResponseDto;
import com.poppin.poppinserver.user.service.BlockUserService;
import com.poppin.poppinserver.user.service.UserFaqService;
import com.poppin.poppinserver.user.service.UserHardDeleteService;
import com.poppin.poppinserver.user.service.UserPreferenceSettingService;
import com.poppin.poppinserver.user.service.UserProfileImageService;
import com.poppin.poppinserver.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController implements SwaggerUserController {
    private final AlarmService alarmService;
    private final AlarmSettingService alarmSettingService;
    private final UserService userService;
    private final SearchPopupService searchPopupService;
    private final BlockUserService blockUserService;
    private final UserPreferenceSettingService userPreferenceSettingService;
    private final UserProfileImageService userProfileImageService;
    private final UserHardDeleteService userHardDeleteService;
    private final UserFaqService userFaqService;

    /**
     * 마이페이지 - 취향 설정 수정
     */
    @PutMapping("/popup-taste")
    public ResponseDto<UserPreferenceUpdateResponseDto> updateUserTaste(
            @UserId Long userId,
            @RequestBody @Valid CreateUserTasteDto userTasteDto
    ) {
        return ResponseDto.ok(userPreferenceSettingService.updateUserPreference(userId, userTasteDto));
    }

    /**
     * 마이페이지 - 프로필 수정: 프로필 사진, 닉네임
     */
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

    /**
     * 마이페이지 - 프로필 삭제: 프로필 사진 삭제
     */
    @DeleteMapping("/image")
    public ResponseDto<String> deleteUserProfileImage(@UserId Long userId) {
        userProfileImageService.deleteProfileImage(userId);
        return ResponseDto.ok("프로필 이미지가 삭제되었습니다.");
    }

    /**
     * 마이페이지 - 회원 탈퇴
     */
    @DeleteMapping("/withdrawal")
    public ResponseDto<String> deleteUser(@UserId Long userId) {
        userHardDeleteService.deleteUser(userId);
        return ResponseDto.ok("회원 탈퇴가 완료되었습니다.");
    }

    /**
     * 마이페이지 - 일반 후기 팝업 검색
     */
    @GetMapping("/popup/search")
    public ResponseDto<PagingResponseDto<List<PopupStoreDto>>> searchPopupName(
            @RequestParam("searchName") String searchName,
            @RequestParam("filteringThreeCategories") String filteringThreeCategories,
            @RequestParam("filteringFourteenCategories") String filteringFourteenCategories,
            @RequestParam("operationStatus") EOperationStatus oper,
            @RequestParam("order") EPopupSort order,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            HttpServletRequest request) {
        return ResponseDto.ok(
                searchPopupService.readSearchingList(searchName, filteringThreeCategories, filteringFourteenCategories,
                        oper, order, page, size, request));
    }

    /**
     * 자주 묻는 질문 조회
     */
    @GetMapping("/support/faqs")
    public ResponseDto<List<UserFaqResponseDto>> readFAQs() {
        return ResponseDto.ok(userFaqService.readFAQs());
    }

    /**
     * 한글 닉네임 랜덤 생성
     */
    @GetMapping("/random-nickname")
    public ResponseDto<UserNicknameResponseDto> generateRandomNickname() {
        return ResponseDto.ok(userService.generateRandomNickname());
    }

    /**
     * 마이페이지 - 프로필 삭제: 프로필 사진 삭제
     */
    @PostMapping("/block/{blockUserId}")
    public ResponseDto<String> createBlockedUser(@UserId Long userId, @PathVariable Long blockUserId) {
        blockUserService.createBlockedUser(userId, blockUserId);
        return ResponseDto.ok("차단 완료되었습니다.");
    }

    /**
     * 알람 확인 표시
     */
    @PatchMapping("/notifications/check")
    public ResponseDto<String> checkNotification(@UserId Long userId,
                                                 @RequestBody NotificationRequestDto notificationRequestDto) {
        return ResponseDto.ok(alarmService.checkNotification(userId, notificationRequestDto));
    }

    /**
     * 알람 설정 수정
     */
    @PatchMapping("/notifications/settings")
    public ResponseDto<UserNotificationSettingResponseDto> updateAlarmSetting(
            @UserId Long userId,
            @RequestBody AlarmSettingRequestDto dto
    ) {
        return ResponseDto.ok(alarmSettingService.updateAlarmSetting(userId, dto));
    }

}
