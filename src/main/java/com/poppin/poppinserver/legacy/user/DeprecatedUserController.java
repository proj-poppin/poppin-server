//package com.poppin.poppinserver.legacy.user;
//
//import com.poppin.poppinserver.core.annotation.UserId;
//import com.poppin.poppinserver.core.dto.ResponseDto;
//import com.poppin.poppinserver.user.dto.user.request.CreateUserTasteDto;
//import com.poppin.poppinserver.user.service.UserPreferenceSettingService;
//import jakarta.validation.Valid;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestPart;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/users")
//@Slf4j
//public class DeprecatedUserController {
//    private final DeprecatedUserService userService;
//    private final UserPreferenceSettingService userPreferenceSettingService;
//
//    // TODO: 삭제 예정
//    @GetMapping("")
//    public ResponseDto<?> readUser(@UserId Long userId) {
//        return ResponseDto.ok(userService.readUser(userId));
//    }
//
//    // TODO: 삭제 예정
//    @GetMapping("/settings")
//    public ResponseDto<?> readUserProfile(@UserId Long userId) {
//        return ResponseDto.ok(userService.readUserProfile(userId));
//    }
//
//    // TODO: 삭제 예정
//    @PostMapping("/popup-taste")
//    public ResponseDto<?> createUserTaste(
//            @UserId Long userId,
//            @RequestBody @Valid CreateUserTasteDto userTasteDto
//    ) {
//        return ResponseDto.created(userPreferenceSettingService.createUserTaste(userId, userTasteDto));
//    }
//
//    // TODO: 삭제 예정
//    @GetMapping("/popup-taste")
//    public ResponseDto<?> readUserTaste(@UserId Long userId) {
//        return ResponseDto.ok(userPreferenceSettingService.readUserTaste(userId));
//    }
//
//    // TODO: 삭제 예정
//    @GetMapping("/preference-setting")
//    public ResponseDto<?> readUserPreferenceSettingCreated(@UserId Long userId) {
//        return ResponseDto.ok(userService.readUserPreferenceSettingCreated(userId));
//    }
//
//    //TODO: 삭제 예정
//    /*마이페이지 - 후기 작성하기 - 방문한 팝업 조회*/
//    @GetMapping("popup/v/certi")
//    public ResponseDto<?> getCertifiedPopupList(@UserId Long userId) {
//        return ResponseDto.ok(userService.getCertifiedPopupList(userId));
//    }
//
//    //TODO: 삭제 예정
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
//    // TODO: 삭제 예정
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
//
//    // TODO: 삭제 예정
//    /*작성완료 후기 조회*/
//    @GetMapping("/review/finish")
//    public ResponseDto<?> getFinishReviewList(@UserId Long userId) {
//        return ResponseDto.ok(userService.getFinishReviewList(userId));
//    }
//
//    // TODO: 삭제 예정
//    /*작성완료 인증후기 보기*/
//    @GetMapping("/review/finish/certi")
//    public ResponseDto<?> getCertifiedReview(@UserId Long userId, @RequestParam(value = "reviewId") Long reviewId,
//                                             @RequestParam(value = "popupId") Long popupId) {
//        return ResponseDto.ok(userService.getCertifiedReview(userId, reviewId, popupId));
//    }
//
//    // TODO: 삭제 예정
//    /*작성완료 미인증후기 보기*/
//    @GetMapping("/review/finish/uncerti")
//    public ResponseDto<?> getUncertifiedReview(@UserId Long userId, @RequestParam(value = "reviewId") Long reviewId,
//                                               @RequestParam(value = "popupId") Long popupId) {
//        return ResponseDto.ok(userService.getUncertifiedReview(userId, reviewId, popupId));
//    }
//}
