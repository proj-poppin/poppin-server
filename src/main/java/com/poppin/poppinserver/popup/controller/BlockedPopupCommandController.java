package com.poppin.poppinserver.popup.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.popup.controller.swagger.SwaggerBlockedPopupCommandController;
import com.poppin.poppinserver.popup.dto.blockedPopup.response.BlockedPopupDto;
import com.poppin.poppinserver.popup.service.BlockedPopupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//팝업차단
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/popup/block")
@ConditionalOnProperty(name = "app.write.enabled", havingValue = "true")
public class BlockedPopupCommandController implements SwaggerBlockedPopupCommandController {
    private final BlockedPopupService blockedPopupService;

    // 팝업 차단
    @PostMapping("/{blockPostId}")
    public ResponseDto<BlockedPopupDto> createdBlockedPopup(@PathVariable String blockPostId,
                                                            @UserId Long userId) {
        return ResponseDto.ok(blockedPopupService.createBlockedPopup(blockPostId, userId));
    }
}
