package com.poppin.poppinserver.user.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.core.type.EOperationStatus;
import com.poppin.poppinserver.core.type.EPopupSort;
import com.poppin.poppinserver.popup.service.PopupService;
import com.poppin.poppinserver.review.service.ReviewService;
import com.poppin.poppinserver.user.dto.user.request.CreateUserTasteDto;
import com.poppin.poppinserver.user.dto.user.request.UpdateUserInfoDto;
import com.poppin.poppinserver.user.service.BlockUserService;
import com.poppin.poppinserver.user.service.UserPreferenceSettingService;
import com.poppin.poppinserver.user.service.UserService;
import com.poppin.poppinserver.user.usecase.UserExampleUsecase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
public class UserController {
    private final UserService userService;
    private final ReviewService reviewService;
    private final PopupService popupService;
    private final BlockUserService blockUserService;
    private final UserPreferenceSettingService userPreferenceSettingService;
    private final UserExampleUsecase userExampleUsecase;

    // TODO: 삭제 예정
    @PostMapping("/popup-taste")
    public ResponseDto<?> createUserTaste(
            @UserId Long userId,
            @RequestBody @Valid CreateUserTasteDto userTasteDto
    ) {
        userExampleUsecase.test();
        return ResponseDto.created(userPreferenceSettingService.createUserTaste(userId, userTasteDto));
    }

    // TODO: 삭제 예정
    @GetMapping("/popup-taste")
    public ResponseDto<?> readUserTaste(@UserId Long userId) {
        return ResponseDto.ok(userPreferenceSettingService.readUserTaste(userId));
    }

    @PutMapping("/popup-taste")
    public ResponseDto<?> updateUserTaste(
            @UserId Long userId,
            @RequestBody @Valid CreateUserTasteDto userTasteDto
    ) {
        return ResponseDto.ok(userPreferenceSettingService.updateUserTaste(userId, userTasteDto));
    }

    // TODO: 삭제 예정
    @GetMapping("")
    public ResponseDto<?> readUser(@UserId Long userId) {
        return ResponseDto.ok(userService.readUser(userId));
    }

    // TODO: 삭제 예정
    @GetMapping("/settings")
    public ResponseDto<?> readUserProfile(@UserId Long userId) {
        return ResponseDto.ok(userService.readUserProfile(userId));
    }

    @PostMapping("/image")
    public ResponseDto<?> createUserProfileImage(@UserId Long userId,
                                                 @RequestPart(value = "profileImage") MultipartFile profileImage) {
        return ResponseDto.created(userService.createProfileImage(userId, profileImage));
    }

    @PutMapping("/image")
    public ResponseDto<?> updateUserProfileImage(@UserId Long userId,
                                                 @RequestPart(value = "profileImage") MultipartFile profileImage) {
        return ResponseDto.ok(userService.updateProfileImage(userId, profileImage));
    }

    @DeleteMapping("/image")
    public ResponseDto<?> deleteUserProfileImage(@UserId Long userId) {
        userService.deleteProfileImage(userId);
        return ResponseDto.ok("프로필 이미지가 삭제되었습니다.");
    }

    @PutMapping("/settings")
    public ResponseDto<?> updateUserNickname(
            @UserId Long userId,
            @RequestBody UpdateUserInfoDto updateUserInfoDto
    ) {
        return ResponseDto.ok(userService.updateUserNickname(userId, updateUserInfoDto));
    }

    @DeleteMapping("/withdrawal")
    public ResponseDto<?> deleteUser(@UserId Long userId) {
        userService.deleteUser(userId);
        return ResponseDto.ok("회원 탈퇴가 완료되었습니다.");
    }

    // TODO: 삭제 예정
    /*작성완료 후기 조회*/
    @GetMapping("/review/finish")
    public ResponseDto<?> getFinishReviewList(@UserId Long userId) {
        return ResponseDto.ok(userService.getFinishReviewList(userId));
    }

    // TODO: 삭제 예정
    /*작성완료 인증후기 보기*/
    @GetMapping("/review/finish/certi")
    public ResponseDto<?> getCertifiedReview(@UserId Long userId, @RequestParam(value = "reviewId") Long reviewId,
                                             @RequestParam(value = "popupId") Long popupId) {
        return ResponseDto.ok(userService.getCertifiedReview(userId, reviewId, popupId));
    }

    // TODO: 삭제 예정
    /*작성완료 미인증후기 보기*/
    @GetMapping("/review/finish/uncerti")
    public ResponseDto<?> getUncertifiedReview(@UserId Long userId, @RequestParam(value = "reviewId") Long reviewId,
                                               @RequestParam(value = "popupId") Long popupId) {
        return ResponseDto.ok(userService.getUncertifiedReview(userId, reviewId, popupId));
    }

    // TODO: 삭제 예정
    /*마이페이지 - 후기 작성하기 - 방문한 팝업 조회*/
    @GetMapping("popup/v/certi")
    public ResponseDto<?> getCertifiedPopupList(@UserId Long userId) {
        return ResponseDto.ok(userService.getCertifiedPopupList(userId));
    }

//    /*마이페이지 - 후기 작성하기 - 방문한 팝업 후기 작성*/
//    @PostMapping(value = "review/w/certi", consumes = {MediaType.APPLICATION_JSON_VALUE,
//            MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseDto<?> createMPCertiReview(
//            @UserId Long userId,
//            @RequestParam("fcmToken") String token,
//            @RequestParam("popupId") Long popupId,
//            @RequestParam("text") String text,
//            @RequestParam("visitDate") String visitDate,
//            @RequestParam("satisfaction") String satisfaction,
//            @RequestParam("congestion") String congestion,
//            @RequestParam("nickname") String nickname,
//            @RequestPart(value = "images") List<MultipartFile> images) {
//        return ResponseDto.ok(
//                reviewService.writeCertifiedReview(userId, token, popupId, text, visitDate, satisfaction, congestion,
//                        nickname, images));
//    }
//
//    /*마이페이지 - 후기 작성하기 - 일반 후기 작성*/
//    @PostMapping(value = "review/w/uncerti", consumes = {MediaType.APPLICATION_JSON_VALUE,
//            MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseDto<?> createMPUncertiReview(
//            @UserId Long userId,
//            @RequestParam("fcmToken") String token,
//            @RequestParam("popupId") Long popupId,
//            @RequestParam("text") String text,
//            @RequestParam("visitDate") String visitDate,
//            @RequestParam("satisfaction") String satisfaction,
//            @RequestParam("congestion") String congestion,
//            @RequestParam("nickname") String nickname,
//            @RequestPart(value = "images") List<MultipartFile> images) {
//        return ResponseDto.ok(
//                reviewService.writeUncertifiedReview(userId, token, popupId, text, visitDate, satisfaction, congestion,
//                        nickname, images));
//    }

    /*마이페이지 - 일반후기 팝업 검색*/
    @GetMapping("/popup/search")
    public ResponseDto<?> searchPopupName(@RequestParam("text") String text,
                                          @RequestParam("taste") String taste,
                                          @RequestParam("prepered") String prepered,
                                          @RequestParam("oper") EOperationStatus oper,
                                          @RequestParam("order") EPopupSort order,
                                          @RequestParam("page") int page,
                                          @RequestParam("size") int size,
                                          @UserId Long userId) {
        return ResponseDto.ok(popupService.readSearchingList(text, taste, prepered, oper, order, page, size, userId));
    }

    /*마이페이지 - 자주 묻는 질문 조회*/
    @GetMapping("/support/faqs")
    public ResponseDto<?> readFAQs() {
        return ResponseDto.ok(userService.readFAQs());
    }

    /*마이페이지 - 한글 닉네임 랜덤 생성*/
    @GetMapping("/random-nickname")
    public ResponseDto<?> generateRandomNickname() {
        return ResponseDto.ok(userService.generateRandomNickname());
    }

    // TODO: 삭제 예정
//    @GetMapping("/preference-setting")
//    public ResponseDto<?> readUserPreferenceSettingCreated(@UserId Long userId) {
//        return ResponseDto.ok(userService.readUserPreferenceSettingCreated(userId));
//    }

    @PostMapping("/block/{blockUserId}")
    public ResponseDto<?> createBlockedUser(@UserId Long userId, @PathVariable Long blockUserId) {
        blockUserService.createBlockedUser(userId, blockUserId);
        return ResponseDto.ok("차단 완료되었습니다.");
    }
}
