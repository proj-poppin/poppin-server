package com.poppin.poppinserver.popup.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.popup.controller.swagger.SwaggerPopupCommandController;
import com.poppin.poppinserver.popup.dto.popup.request.CreatePopupDto;
import com.poppin.poppinserver.popup.dto.popup.request.UpdatePopupDto;
import com.poppin.poppinserver.popup.dto.popup.request.VisitorsInfoDto;
import com.poppin.poppinserver.popup.dto.popup.response.AdminPopupDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupStoreDto;
import com.poppin.poppinserver.popup.service.AdminPopupService;
import com.poppin.poppinserver.popup.service.PopupService;
import com.poppin.poppinserver.visit.service.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// 팝업
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/popup")
public class PopupCommandController implements SwaggerPopupCommandController {
    private final PopupService popupService;

    private final AdminPopupService adminPopupService;
    private final VisitService visitService;

    @PostMapping(value = "/admin", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<AdminPopupDto> createPopup(@RequestPart(value = "images") List<MultipartFile> images,
                                                  @RequestPart(value = "contents") @Valid CreatePopupDto createPopupDto,
                                                  @UserId Long adminId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(adminPopupService.createPopup(createPopupDto, images, adminId));
    } // 전체팝업관리 - 팝업생성

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin")
    public ResponseDto<Boolean> removePopup(@RequestParam("id") Long popupId,
                                      @UserId Long adminId) throws FirebaseMessagingException {
        return ResponseDto.ok(adminPopupService.removePopup(popupId));
    } // 전체팝업관리 - 팝업 삭제

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/admin", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<AdminPopupDto> uploadManagerInform(@RequestPart(value = "images") List<MultipartFile> images,
                                              @RequestPart(value = "contents") @Valid UpdatePopupDto updatePopupDto,
                                              @UserId Long adminId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(adminPopupService.updatePopup(updatePopupDto, images, adminId));
    } // 전체팝업관리 - 팝업 수정

    @PatchMapping("/visit") // 팝업 방문하기
    public ResponseDto<PopupStoreDto> visit(@UserId Long userId, @RequestBody VisitorsInfoDto visitorsInfoDto) throws FirebaseMessagingException {
        return ResponseDto.ok(visitService.visit(userId, visitorsInfoDto));
    }

    @PostMapping("/reopen") // 재오픈 신청
    public ResponseDto<String> reopen(@UserId Long userId,  @RequestBody String popupId) throws FirebaseMessagingException {
        return ResponseDto.ok(popupService.reopenDemand(userId, popupId));
    }

}
