package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.blockedPopup.request.CreateBlockedPopup;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.service.BlockedPopupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/popup/block")
public class BlockedPopupController {
    private final BlockedPopupService blockedPopupService;
    @PostMapping("/{blockPostId}")
    public ResponseDto<?> createdBlockedPopup(@PathVariable Long blockPostId,
                                              @UserId Long userId) {
        return ResponseDto.ok(blockedPopupService.createBlockedPopup(blockPostId, userId));
    }
}
