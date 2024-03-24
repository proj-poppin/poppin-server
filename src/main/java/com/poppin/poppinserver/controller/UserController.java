package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.dto.popup.request.CreateUserTasteDto;
import com.poppin.poppinserver.dto.popup.request.CreateWhoWithDto;
import com.poppin.poppinserver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/taste")
    public ResponseDto<?> createUserTaste(
            @UserId Long userId,
            @RequestBody @Valid CreateUserTasteDto userTasteDto
    ) {
        return ResponseDto.created(userService.createUserTaste(userId, userTasteDto));
    }

    @GetMapping("/taste")
    public ResponseDto<?> readUserTaste(@UserId Long userId) {
        return ResponseDto.ok(userService.readUserTaste(userId));
    }
}
