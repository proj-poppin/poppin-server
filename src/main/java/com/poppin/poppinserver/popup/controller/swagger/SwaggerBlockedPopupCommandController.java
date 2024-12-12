package com.poppin.poppinserver.popup.controller.swagger;

import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.popup.dto.blockedPopup.response.BlockedPopupDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "팝업 차단 관리", description = "팝업 차단 관리 API")
public interface SwaggerBlockedPopupCommandController {

    @Operation(summary = "팝업 차단 생성", description = "특정 팝업을 차단합니다.")
    @PostMapping("/{blockPostId}")
    ResponseDto<BlockedPopupDto> createdBlockedPopup(
            @PathVariable String blockPostId,
            @Parameter(hidden = true) Long userId
    );
}
