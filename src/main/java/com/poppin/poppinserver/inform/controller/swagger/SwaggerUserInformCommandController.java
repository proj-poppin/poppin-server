package com.poppin.poppinserver.inform.controller.swagger;

import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.inform.dto.userInform.request.UpdateUserInformDto;
import com.poppin.poppinserver.inform.dto.userInform.response.UserInformDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "사용자 제보", description = "사용자 제보 관련 API")
public interface SwaggerUserInformCommandController {

    @Operation(summary = "사용자 제보 생성", description = "사용자로부터 제보를 생성합니다.")
    ResponseDto<UserInformDto> createUserInform(
            @RequestPart(value = "images") List<MultipartFile> images,
            @RequestParam(value = "storeName") String storeName,
            @RequestParam(value = "contactLink", required = false) String contactLink,
            @RequestParam("filteringFourteenCategories") String filteringFourteenCategories,
            HttpServletRequest request
    );

    @Operation(summary = "관리자 - 사용자 제보 임시 저장", description = "관리자가 사용자 제보를 임시 저장합니다.")
    ResponseDto<UserInformDto> saveUserInform(
            @RequestPart(value = "images") List<MultipartFile> images,
            @RequestPart(value = "contents") @Valid UpdateUserInformDto updateUserInformDto,
            Long adminId
    );

    @Operation(summary = "관리자 - 사용자 제보 업로드 승인", description = "관리자가 사용자 제보를 최종 승인합니다.")
    ResponseDto<UserInformDto> uploadUserInform(
            @RequestPart(value = "images") List<MultipartFile> images,
            @RequestPart(value = "contents") @Valid UpdateUserInformDto updateUserInformDto,
            Long adminId
    );
}
