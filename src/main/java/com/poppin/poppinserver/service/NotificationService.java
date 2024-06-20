package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.NotificationToken;
import com.poppin.poppinserver.dto.notification.request.TokenRequestDto;
import com.poppin.poppinserver.dto.notification.response.TokenResponseDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.NotificationTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationTokenRepository notificationTokenRepository;


    /* 알림 동의 - 공지사항 , 팝업은 따로 저장 해야 함 */
    public TokenResponseDto fcmApplyToken(TokenRequestDto tokenRequestDto){

        try {

            // 토큰 저장 여부 확인
            NotificationToken isToken = notificationTokenRepository.findByToken(tokenRequestDto.token());
            if (isToken!=null) throw new CommonException(ErrorCode.DUPLICATED_TOKEN);
            else{
                // 토큰 저장
                log.info("디바이스 정보 : " + tokenRequestDto.device());
                NotificationToken notificationToken = new NotificationToken(
                        tokenRequestDto.token(),
                        LocalDateTime.now(), // 토큰 등록 시간 + 토큰 만기 시간(+2달)
                        tokenRequestDto.device() // android or ios
                );
                NotificationToken token = notificationTokenRepository.save(notificationToken); // 토큰 저장
                return TokenResponseDto.fromEntity(tokenRequestDto, "token 저장 성공" , token.getToken());
            }
        }catch (Exception e){
           log.error("토큰 등록 실패: " + e.getMessage());
            return TokenResponseDto.fromEntity(tokenRequestDto, "token 저장 실패" , e.getMessage());
        }
    }

}
