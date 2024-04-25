package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.service.NotificationService;
import com.poppin.poppinserver.util.NotificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/noti")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationUtil notificationUtil;

    @PostMapping("/android/test")
    public void sendNotificationByTokenTest(String token , String title, String content){
        notificationUtil.sendNotificationByTokenTest(token, title, content);
    }
}
