package com.poppin.poppinserver.popup.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.popup.service.BlockedPopupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/popup/block")
@ConditionalOnProperty(name = "app.write.enabled", havingValue = "true")
public class BlockedPopupCommandController {
    private final BlockedPopupService blockedPopupService;

    @PostMapping("/{blockPostId}")
    public ResponseDto<?> createdBlockedPopup(@PathVariable String blockPostId,
                                              @UserId Long userId) {
        return ResponseDto.ok(blockedPopupService.createBlockedPopup(blockPostId, userId));
    }
}
