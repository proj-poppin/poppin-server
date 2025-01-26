package com.poppin.poppinserver.user.controller.swagger;

import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmSettingRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.request.NotificationRequestDto;
import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.core.type.EOperationStatus;
import com.poppin.poppinserver.core.type.EPopupSort;
import com.poppin.poppinserver.popup.dto.popup.response.PopupStoreDto;
import com.poppin.poppinserver.user.dto.user.request.CreateUserTasteDto;
import com.poppin.poppinserver.user.dto.user.response.UserFaqResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserNicknameResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserNotificationSettingResponseDto;
import com.poppin.poppinserver.user.dto.user.response.UserPreferenceUpdateResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "사용자", description = "사용자 관련 API")
public interface SwaggerUserController {

//    @Operation(summary = "사용자 취향 생성", description = "사용자의 취향을 생성합니다.")
//    @PostMapping("/popup-taste")
//    ResponseDto<?> createUserTaste(
//            @Parameter(hidden = true) Long userId,
//            @RequestBody CreateUserTasteDto userTasteDto
//    );

//    @Operation(summary = "사용자 취향 조회", description = "사용자의 취향을 조회합니다.")
//    @GetMapping("/popup-taste")
//    ResponseDto<?> readUserTaste(
//            @Parameter(hidden = true) Long userId
//    );

    @Operation(summary = "사용자 취향 수정", description = "사용자의 취향을 수정합니다.")
    @PutMapping("/popup-taste")
    ResponseDto<UserPreferenceUpdateResponseDto> updateUserTaste(
            @Parameter(hidden = true) Long userId,
            @RequestBody CreateUserTasteDto userTasteDto
    );

    @Operation(summary = "알림 설정 수정", description = "사용자의 알림 설정을 수정합니다.")
    @PatchMapping("/notifications/settings")
    ResponseDto<UserNotificationSettingResponseDto> updateAlarmSetting(
            @Parameter(hidden = true) Long userId,
            @RequestBody AlarmSettingRequestDto dto
    );

    @Operation(summary = "알림 설정 수정", description = "사용자의 알림 설정을 수정합니다.")
    @PatchMapping("/notifications/check")
    ResponseDto<String> checkNotification(
            @Parameter(hidden = true) Long userId,
            @RequestBody NotificationRequestDto dto
    );

//    @Operation(summary = "사용자 프로필 이미지 생성", description = "사용자 프로필 이미지를 생성합니다.")
//    @PostMapping("/image")
//    ResponseDto<String> createUserProfileImage(
//            @Parameter(hidden = true) Long userId,
//            @RequestPart(value = "profileImage") MultipartFile profileImage
//    );

    @Operation(summary = "사용자 프로필 수정", description = "사용자 프로필을 수정합니다.")
    @PatchMapping(value = "/profile", consumes = {"application/json", "multipart/form-data"})
    ResponseDto<String> updateUserProfile(
            @Parameter(hidden = true) Long userId,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "nickname") String nickname
    );

    @Operation(summary = "사용자 프로필 이미지 삭제", description = "사용자 프로필 이미지를 삭제합니다.")
    @DeleteMapping("/image")
    ResponseDto<String> deleteUserProfileImage(
            @Parameter(hidden = true) Long userId
    );

//    @Operation(summary = "사용자 닉네임 수정", description = "사용자의 닉네임을 수정합니다.")
//    @PutMapping("/settings")
//    ResponseDto<UserProfileDto> updateUserNickname(
//            @Parameter(hidden = true) Long userId,
//            @RequestBody UpdateUserInfoDto updateUserInfoDto
//    );

    @Operation(summary = "회원 탈퇴", description = "사용자가 회원 탈퇴를 진행합니다.")
    @DeleteMapping("/withdrawal")
    ResponseDto<String> deleteUser(
            @Parameter(hidden = true) Long userId
    );

    @Operation(summary = "팝업 검색", description = "마이페이지에서 팝업을 검색합니다.")
    @GetMapping("/popup/search")
    ResponseDto<PagingResponseDto<List<PopupStoreDto>>> searchPopupName(
            @RequestParam("text") String text,
            @Parameter(example = "market,display,experience") @RequestParam("filteringThreeCategories") String filteringThreeCategories,
            @Parameter(example = "fashionBeauty,characters,foodBeverage,webtoonAni,interiorThings,movie,musical,sports,game,itTech,kpop,alcohol,animalPlant,etc") @RequestParam("filteringFourteenCategories") String filteringFourteenCategories,
            @RequestParam("oper") EOperationStatus oper,
            @RequestParam("order") EPopupSort order,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            HttpServletRequest request
    );

    @Operation(summary = "자주 묻는 질문 조회", description = "마이페이지에서 자주 묻는 질문을 조회합니다.")
    @GetMapping("/support/faqs")
    ResponseDto<List<UserFaqResponseDto>> readFAQs();

    @Operation(summary = "랜덤 닉네임 생성", description = "한글로 랜덤 닉네임을 생성합니다.")
    @GetMapping("/random-nickname")
    ResponseDto<UserNicknameResponseDto> generateRandomNickname();

    @Operation(summary = "사용자 차단", description = "특정 사용자를 차단합니다.")
    @PostMapping("/block/{blockUserId}")
    ResponseDto<String> createBlockedUser(
            @Parameter(hidden = true) Long userId,
            @PathVariable Long blockUserId
    );
}
