package com.poppin.poppinserver.test;


import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.poppin.poppinserver.alarm.repository.FCMTokenRepository;
import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {
    private final UserQueryRepository userQueryRepository;
    private final FCMTokenRepository fcmTokenRepository;

    @PostMapping("/fcm")
    public ResponseDto<?> fcm(
            @UserId Long userId
    ) {
        // User user = userQueryRepository.findById(userId).orElse(null);
        System.err.println("fcm token: " + fcmTokenRepository.findByUserId(userId).orElse(null));
        sendMessage("알람 테스트", "성공하면 말하셈",
                "dw-FN7LMwUq_vo8ZG5YkCK:APA91bFF1o1b3TR0d48o1gvHwkuFicMArUMerMymJyGoguqm9ZRHErHhlDPD_8sbIiSC7pWiiR_RsR6n9LER2aUBHlvO2Ci8p9qDKHcjqmK4HrlIys-A608");
        return ResponseDto.ok(true);
    }

    public void sendMessage(String title, String body, String token) {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                //.putData("test", "test")
                .build();

        String response = null;
        try {
            response = FirebaseMessaging.getInstance().sendAsync(message).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new CommonException(ErrorCode.SERVER_ERROR);
        }
        System.out.println("message " + response);
    }
}
