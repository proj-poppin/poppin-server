package com.poppin.poppinserver.popup.controller.swagger;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.popup.dto.popup.request.CreatePopupDto;
import com.poppin.poppinserver.popup.dto.popup.request.UpdatePopupDto;
import com.poppin.poppinserver.popup.dto.popup.request.VisitorsInfoDto;
import com.poppin.poppinserver.popup.dto.popup.response.AdminPopupDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupStoreDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "팝업 관리", description = "팝업 관리 관련 API")
public interface SwaggerPopupCommandController {

    @Operation(summary = "관리자 - 팝업 생성", description = "관리자가 새로운 팝업을 생성합니다.")
    @PostMapping(value = "/admin", consumes = {"application/json", "multipart/form-data"})
    ResponseDto<AdminPopupDto> createPopup(
            @RequestPart(value = "images") List<MultipartFile> images,
            @RequestPart(value = "contents") CreatePopupDto createPopupDto,
            @Parameter(hidden = true) Long adminId
    );

    @Operation(summary = "관리자 - 팝업 삭제", description = "관리자가 특정 팝업을 삭제합니다.")
    @DeleteMapping("/admin")
    ResponseDto<Boolean> removePopup(
            @RequestParam("id") Long popupId,
            @Parameter(hidden = true) Long adminId
    ) throws FirebaseMessagingException;

    @Operation(summary = "관리자 - 팝업 수정", description = "관리자가 팝업을 수정합니다.")
    @PutMapping(value = "/admin", consumes = {"application/json", "multipart/form-data"})
    ResponseDto<AdminPopupDto> uploadManagerInform(
            @RequestPart(value = "images") List<MultipartFile> images,
            @RequestPart(value = "contents") UpdatePopupDto updatePopupDto,
            @Parameter(hidden = true) Long adminId
    );

    @Operation(summary = "팝업 방문", description = "사용자가 특정 팝업을 방문합니다.")
    @PatchMapping("/visit")
    ResponseDto<PopupStoreDto> visit(
            @Parameter(hidden = true) Long userId,
            @RequestBody VisitorsInfoDto visitorsInfoDto
    ) throws FirebaseMessagingException;

    @Operation(summary = "재오픈 요청", description = "사용자가 재오픈 요청을 보냅니다.")
    @PostMapping("/reopen") // 재오픈 신청
    public ResponseDto<String> reopen(@UserId Long userId, @RequestBody String popupId) throws FirebaseMessagingException;

}
